#!/usr/bin/env bash
# Tests scripts/release.sh end to end without touching GitHub or the real remote.
#
# Each test builds a throwaway sandbox:
#   origin.git  bare repo standing in for GitHub (master, develop, tag v1.3.0)
#   work/       clone of origin.git where release.sh runs
#   bin/gh      fake gh CLI: logs every call, records PRs, and "merges" a PR into
#               origin.git the first time it is viewed after creation
#
# Prompts are answered by piping canned input to release.sh.
#
# Usage: scripts/test-release.sh [TEST_NAME...]   (default: all tests)
set -uo pipefail

REPO=$(cd "$(dirname "$0")/.." && pwd)
SCRIPT="$REPO/scripts/release.sh"

# Isolate git from the user's config (signing, hooks, aliases) and give it an identity.
export GIT_CONFIG_GLOBAL=/dev/null GIT_CONFIG_NOSYSTEM=1
export GIT_AUTHOR_NAME="Release Test" GIT_AUTHOR_EMAIL="test@example.com"
export GIT_COMMITTER_NAME="Release Test" GIT_COMMITTER_EMAIL="test@example.com"

FAILURES=0
CURRENT=""

# ---------- assertions ----------
fail() { echo "  FAIL: $*"; FAILURES=$((FAILURES + 1)); }
pass() { echo "  ok:   $*"; }

assert_eq() { # expected actual description
  if [[ "$1" == "$2" ]]; then pass "$3"; else fail "$3 (expected '$1', got '$2')"; fi
}
assert_contains() { # file pattern description
  if grep -qE -- "$2" "$1"; then pass "$3"; else fail "$3 (no match for /$2/ in $(basename "$1"))"; fi
}
assert_not_contains() {
  if grep -qE -- "$2" "$1"; then fail "$3 (unexpected match for /$2/)"; else pass "$3"; fi
}
assert_true() { # description command...
  local desc="$1"; shift
  if "$@"; then pass "$desc"; else fail "$desc"; fi
}
assert_false() { # description command...
  local desc="$1"; shift
  if "$@"; then fail "$desc"; else pass "$desc"; fi
}

# ---------- sandbox ----------
origin_git() { git --git-dir="$SANDBOX/origin.git" "$@"; }
origin_revision() { origin_git show "$1:pom.xml" | sed -n 's:.*<revision>\(.*\)</revision>.*:\1:p'; }
origin_has_ref() { origin_git rev-parse -q --verify "$1" >/dev/null; }
count_calls() { grep -cE -- "$1" "$SANDBOX/gh/calls.log" || true; }

write_pom() { # revision
  cat > pom.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>test</groupId>
    <artifactId>release-sandbox</artifactId>
    <version>\${revision}</version>
    <packaging>pom</packaging>
    <properties>
        <revision>$1</revision>
    </properties>
</project>
EOF
}

setup_sandbox() {
  SANDBOX=$(mktemp -d "${TMPDIR:-/tmp}/release-test.XXXXXX")
  mkdir -p "$SANDBOX/seed" "$SANDBOX/bin" "$SANDBOX/gh"
  : > "$SANDBOX/gh/calls.log"
  echo 10 > "$SANDBOX/gh/next-pr"

  (
    set -e
    cd "$SANDBOX/seed"
    git init -q -b master
    cp "$REPO/mvnw" . && cp -R "$REPO/.mvn" .
    write_pom 1.3.0
    git add . && git commit -qm "Initial"
    git tag -a v1.3.0 -m "Release 1.3.0"

    git checkout -qb develop
    write_pom 1.4.0-SNAPSHOT
    git commit -qam "Bump to 1.4.0-SNAPSHOT"

    git checkout -qb feature/x
    echo x > feature.txt && git add feature.txt && git commit -qm "Add feature X"
    git checkout -q develop
    git merge -q --no-ff feature/x -m "Merge pull request #2 from test/feature/x"

    git init -q --bare -b master "$SANDBOX/origin.git"
    git push -q "$SANDBOX/origin.git" master develop --tags
    git clone -q "$SANDBOX/origin.git" "$SANDBOX/work"
  ) || { echo "sandbox setup failed"; exit 2; }

  write_fake_gh
}

write_fake_gh() {
  cat > "$SANDBOX/bin/gh" <<'GH'
#!/usr/bin/env bash
# Fake gh. State lives in $FAKE_GH_STATE; PRs are merged into $FAKE_ORIGIN.
# Knobs:
#   FAKE_GH_OPEN_VIEWS=N     an open PR reports OPEN for N views before merging (default 0)
#   FAKE_GH_SQUASH_INTO=BR   squash-merge PRs whose base is BR (default: merge commit)
set -euo pipefail
S="$FAKE_GH_STATE"
echo "gh $*" >> "$S/calls.log"

merge_pr() { # key
  local key="$1" base head num tmp
  base=$(cat "$S/$key.base"); head=$(cat "$S/$key.head"); num=$(cat "$S/$key.num")
  tmp=$(mktemp -d)
  git clone -q "$FAKE_ORIGIN" "$tmp"
  (
    cd "$tmp"
    git checkout -q "$base"
    if [[ "${FAKE_GH_SQUASH_INTO:-}" == "$base" ]]; then
      git merge -q --squash "origin/$head" >/dev/null
      git commit -qm "Squashed $head (#$num)"
    else
      git merge -q --no-ff "origin/$head" -m "Merge pull request #$num from test/$head"
    fi
    git push -q origin "$base"
  )
  rm -rf "$tmp"
  echo MERGED > "$S/$key.state"
}

case "$1" in
  auth|label) exit 0 ;;
  api)
    # Stand-in for releases/generate-notes (already reduced to .body).
    printf '%s\n' "## What's Changed" \
      "* Add feature X by @dev in https://example.test/pull/2" \
      "* Merge master into develop after release 1.3.0 by @dev in https://example.test/pull/3"
    ;;
  pr)
    sub="$2"; shift 2
    case "$sub" in
      create)
        base="" head="" body=""
        while [[ $# -gt 0 ]]; do
          case "$1" in
            --base) base="$2"; shift 2 ;;
            --head) head="$2"; shift 2 ;;
            --body) body="$2"; shift 2 ;;
            *) shift ;;
          esac
        done
        key="pr-${head//\//_}"
        num=$(cat "$S/next-pr"); echo $((num + 1)) > "$S/next-pr"
        echo "$base" > "$S/$key.base"; echo "$head" > "$S/$key.head"; echo "$num" > "$S/$key.num"
        printf '%s\n' "$body" > "$S/$key.body"
        echo OPEN > "$S/$key.state"
        echo 0 > "$S/$key.views"
        echo "https://example.test/pull/$num"
        ;;
      view)
        key="pr-${1//\//_}"
        [[ -f "$S/$key.state" ]] || { echo "no pull requests found for branch \"$1\"" >&2; exit 1; }
        if [[ "$(cat "$S/$key.state")" == OPEN ]]; then
          views=$(cat "$S/$key.views"); echo $((views + 1)) > "$S/$key.views"
          [[ "$views" -ge "${FAKE_GH_OPEN_VIEWS:-0}" ]] && merge_pr "$key"
        fi
        cat "$S/$key.state"
        ;;
      *) echo "fake gh: unsupported 'pr $sub'" >&2; exit 1 ;;
    esac
    ;;
  release)
    tag="$3"
    git --git-dir="$FAKE_ORIGIN" rev-parse -q --verify "refs/tags/$tag" >/dev/null \
      || { echo "fake gh: tag $tag not on origin" >&2; exit 1; }
    ;;
  *) echo "fake gh: unsupported command '$1'" >&2; exit 1 ;;
esac
GH
  chmod +x "$SANDBOX/bin/gh"
}

# run_release ANSWERS [ARGS...] -> sets RC; output in $SANDBOX/out.log
run_release() {
  local answers="$1"; shift
  RC=0
  ( cd "$SANDBOX/work" && printf '%b' "$answers" | \
      PATH="$SANDBOX/bin:$PATH" FAKE_GH_STATE="$SANDBOX/gh" FAKE_ORIGIN="$SANDBOX/origin.git" \
      "$SCRIPT" "$@" ) > "$SANDBOX/out.log" 2>&1 || RC=$?
}

dump_output() { echo "  --- release.sh output ---"; sed 's/^/  | /' "$SANDBOX/out.log"; }

# ---------- tests ----------
test_full_release_with_next_version_arg() {
  run_release 'y\ny\ny\n' 1.5.0
  assert_eq 0 "$RC" "exits 0"
  assert_eq 1.4.0 "$(origin_revision release/1.4.0)" "release branch has release revision"
  assert_eq 1.4.0 "$(origin_revision master)" "master has release revision"
  assert_eq "$(origin_git rev-parse master)" "$(origin_git rev-parse 'v1.4.0^{commit}')" "tag v1.4.0 points at master"
  assert_eq 1.5.0-SNAPSHOT "$(origin_revision develop)" "develop bumped to next snapshot"
  assert_eq "Bump to 1.5.0-SNAPSHOT" "$(origin_git log -1 --format=%s develop)" "bump commit message"
  assert_true "master is ancestor of develop" origin_git merge-base --is-ancestor master develop
  assert_contains "$SANDBOX/gh/calls.log" "previous_tag_name=v1.3.0" "release notes generated since previous tag"
  assert_contains "$SANDBOX/gh/pr-release_1.4.0.body" "Add feature X" "release PR body lists feature PR"
  assert_not_contains "$SANDBOX/gh/pr-release_1.4.0.body" "Merge master into develop" "release PR body drops back-merge PR"
  assert_eq 2 "$(count_calls 'pr create .*--label skip-changelog')" "both PRs labelled skip-changelog"
  assert_contains "$SANDBOX/gh/calls.log" "release create v1.4.0 .*--generate-notes" "GitHub Release created"
  assert_false "no pom backup left behind" test -e "$SANDBOX/work/pom.xml.versionsBackup"
}

test_prompts_for_next_version_until_valid() {
  run_release 'y\ny\ny\n1.5\n1.5.0-SNAPSHOT\n1.5.0\n'
  assert_eq 0 "$RC" "exits 0"
  assert_contains "$SANDBOX/out.log" "Invalid version '1.5'" "rejects X.Y"
  assert_contains "$SANDBOX/out.log" "Invalid version '1.5.0-SNAPSHOT'" "rejects typed -SNAPSHOT"
  assert_eq 1.5.0-SNAPSHOT "$(origin_revision develop)" "develop bumped to entered version"
}

test_resume_after_declining_at_release_pr() {
  run_release 'y\nn\n' 1.5.0
  assert_eq 1 "$RC" "declining exits 1"
  assert_contains "$SANDBOX/out.log" "Resume with: .* --from release-pr 1.5.0" "prints resume command"
  assert_false "no tag yet" origin_has_ref refs/tags/v1.4.0

  run_release 'y\ny\ny\n' --from release-pr 1.5.0
  assert_eq 0 "$RC" "resume exits 0"
  assert_eq 1 "$(count_calls 'pr create --base master')" "release PR not re-created"
  assert_true "tag created on resume" origin_has_ref refs/tags/v1.4.0
  assert_eq 1.5.0-SNAPSHOT "$(origin_revision develop)" "develop bumped"
}

test_reprompts_until_pr_is_merged() {
  FAKE_GH_OPEN_VIEWS=1 run_release 'y\ny\ny\ny\ny\n' 1.5.0
  assert_eq 0 "$RC" "exits 0"
  assert_eq 2 "$(grep -c 'is OPEN, not MERGED' "$SANDBOX/out.log")" "re-prompts once per unmerged PR"
}

test_detects_squashed_back_merge() {
  FAKE_GH_SQUASH_INTO=develop run_release 'y\ny\ny\n' 1.5.0
  assert_eq 1 "$RC" "exits 1"
  assert_contains "$SANDBOX/out.log" "not an ancestor" "reports lost ancestry"
  assert_false "develop not bumped" test "$(origin_revision develop)" = 1.5.0-SNAPSHOT
}

test_refuses_dirty_working_tree() {
  touch "$SANDBOX/work/stray.txt"
  run_release 'y\n'
  assert_eq 1 "$RC" "exits 1"
  assert_contains "$SANDBOX/out.log" "Working tree is not clean" "explains why"
  assert_false "no release branch pushed" origin_has_ref refs/heads/release/1.4.0
}

test_refuses_existing_release_branch() {
  origin_git branch release/1.4.0 develop
  run_release 'y\n'
  assert_eq 1 "$RC" "exits 1"
  assert_contains "$SANDBOX/out.log" "Remote branch release/1.4.0 already exists" "explains why"
  assert_eq 0 "$(count_calls 'pr create')" "no PR opened"
}

test_rejects_malformed_next_version_arg() {
  run_release '' 1.5
  assert_eq 1 "$RC" "exits 1"
  assert_contains "$SANDBOX/out.log" "NEXT_VERSION must be X.Y.Z" "explains why"
}

# ---------- runner ----------
if command -v shellcheck >/dev/null; then
  echo "shellcheck"
  assert_true "release.sh and test-release.sh are clean" shellcheck "$SCRIPT" "$0"
else
  echo "(shellcheck not installed; skipping lint)"
fi

TESTS=("$@")
if [[ ${#TESTS[@]} -eq 0 ]]; then
  while IFS= read -r t; do TESTS+=("$t"); done < <(declare -F | awk '{print $3}' | grep '^test_')
fi

for CURRENT in "${TESTS[@]}"; do
  echo
  echo "$CURRENT"
  setup_sandbox
  before=$FAILURES
  "$CURRENT"
  if [[ $FAILURES -gt $before ]]; then
    dump_output
    echo "  sandbox kept: $SANDBOX"
  else
    rm -rf "$SANDBOX"
  fi
done

echo
if [[ $FAILURES -eq 0 ]]; then echo "All tests passed."; else echo "$FAILURES assertion(s) failed."; exit 1; fi
