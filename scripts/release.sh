#!/usr/bin/env bash
# Automates the release checklist in docs/RELEASING.md.
#
# Usage: scripts/release.sh [--from STEP] [NEXT_VERSION]
#
#   NEXT_VERSION  Next development version as X.Y.Z (e.g. 0.22.0); "-SNAPSHOT" is appended.
#                 If omitted, you are prompted for it at the bump step.
#   --from STEP   Resume from a step: branch | release-pr | tag | back-merge | bump
#
# The release version is always the pom.xml <revision> on origin/develop, minus "-SNAPSHOT".
set -euo pipefail

STEPS=(branch release-pr tag back-merge bump)
SKIP_LABEL="skip-changelog"

die()  { echo "ERROR: $*" >&2; exit 1; }
info() { echo; echo "==> $*"; }

usage() { sed -n '2,10p' "$0" | sed 's/^# \{0,1\}//'; exit "${1:-0}"; }

confirm() {
  local reply
  read -r -p "$1 [y/N] " reply
  [[ "$reply" =~ ^[Yy]$ ]]
}

stop_resumable() {
  echo
  echo "Stopped. Resume with: $0 --from $1${NEXT_VERSION:+ $NEXT_VERSION}"
  exit 1
}

valid_version() { [[ "$1" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; }

step_index() {
  local i
  for i in "${!STEPS[@]}"; do [[ "${STEPS[$i]}" == "$1" ]] && { echo "$i"; return; }; done
  return 1
}

# Revision from a ref's pom.xml without invoking Maven.
pom_revision() {
  git show "$1:pom.xml" | sed -n 's:.*<revision>\(.*\)</revision>.*:\1:p' | head -1
}

set_revision() {
  ./mvnw -q versions:set-property -Dproperty=revision -DnewVersion="$1" -DgenerateBackupPoms=false
}

ensure_label() {
  gh label create "$SKIP_LABEL" --color ededed \
    --description "Excluded from generated release notes" --force >/dev/null
}

# Wait until the PR for a head branch is merged, re-prompting until it is.
wait_for_merge() {
  local branch="$1" step="$2" state
  while true; do
    confirm "Merge the PR for $branch, then continue?" || stop_resumable "$step"
    state=$(gh pr view "$branch" --json state -q .state)
    [[ "$state" == "MERGED" ]] && return
    echo "PR for $branch is $state, not MERGED."
  done
}

# ---------- args ----------
FROM="branch"
NEXT_VERSION=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --from) FROM="${2:-}"; shift 2 ;;
    -h|--help) usage ;;
    -*) die "Unknown option: $1" ;;
    *) [[ -z "$NEXT_VERSION" ]] || die "Unexpected argument: $1"; NEXT_VERSION="$1"; shift ;;
  esac
done
START=$(step_index "$FROM") || die "Unknown step '$FROM' (expected: ${STEPS[*]})"
if [[ -n "$NEXT_VERSION" ]]; then
  valid_version "$NEXT_VERSION" || die "NEXT_VERSION must be X.Y.Z without -SNAPSHOT (got '$NEXT_VERSION')"
fi
should_run() { [[ "$(step_index "$1")" -ge "$START" ]]; }

# ---------- preflight ----------
cd "$(git rev-parse --show-toplevel)"
[[ -z "$(git status --porcelain)" ]] || die "Working tree is not clean."
command -v gh >/dev/null || die "gh CLI not found."
gh auth status >/dev/null 2>&1 || die "gh is not authenticated (run 'gh auth login')."

git fetch --quiet --tags origin
POM_REVISION=$(pom_revision origin/develop)
[[ -n "$POM_REVISION" ]] || die "Could not read <revision> from origin/develop:pom.xml"
VERSION="${POM_REVISION%-SNAPSHOT}"
valid_version "$VERSION" || die "Unexpected revision '$POM_REVISION' on origin/develop"
RELEASE_BRANCH="release/$VERSION"
TAG="v$VERSION"
BACKMERGE_BRANCH="chore/back-merge-master-$VERSION"

if should_run branch; then
  [[ "$POM_REVISION" == *-SNAPSHOT ]] || die "origin/develop revision '$POM_REVISION' is not a -SNAPSHOT."
  git show-ref --quiet "refs/heads/$RELEASE_BRANCH" && die "Local branch $RELEASE_BRANCH already exists."
  git ls-remote --exit-code --heads origin "$RELEASE_BRANCH" >/dev/null && die "Remote branch $RELEASE_BRANCH already exists."
fi
if should_run tag; then
  git show-ref --quiet "refs/tags/$TAG" && die "Local tag $TAG already exists."
  git ls-remote --exit-code --tags origin "$TAG" >/dev/null && die "Remote tag $TAG already exists."
fi

echo "Release version: $VERSION  (starting at step: $FROM)"
confirm "Proceed?" || exit 1

# ---------- steps ----------
if should_run branch; then
  info "[branch] Creating $RELEASE_BRANCH"
  git checkout develop
  git pull --ff-only origin develop
  git checkout -b "$RELEASE_BRANCH"
  set_revision "$VERSION"
  git commit -am "Prepare release $VERSION"
  git push -u origin "$RELEASE_BRANCH"
fi

if should_run release-pr; then
  info "[release-pr] Opening PR $RELEASE_BRANCH -> master"
  ensure_label
  if gh pr view "$RELEASE_BRANCH" --json state -q .state 2>/dev/null | grep -qE 'OPEN|MERGED'; then
    echo "PR already exists for $RELEASE_BRANCH."
  else
    PREV_TAG=$(git describe --tags --abbrev=0 --match 'v*' origin/master)
    NOTES=$(gh api "repos/{owner}/{repo}/releases/generate-notes" \
      -f tag_name="$TAG" -f target_commitish="$RELEASE_BRANCH" -f previous_tag_name="$PREV_TAG" \
      -q .body | grep -vE 'Merge master into develop after release|Prepare release' || true)
    gh pr create --base master --head "$RELEASE_BRANCH" --label "$SKIP_LABEL" \
      --title "Release $VERSION" --body "$NOTES"
  fi
  wait_for_merge "$RELEASE_BRANCH" release-pr
fi

if should_run tag; then
  info "[tag] Tagging $TAG on master"
  git checkout master
  git pull --ff-only origin master
  [[ "$(pom_revision HEAD)" == "$VERSION" ]] || die "master revision is '$(pom_revision HEAD)', expected '$VERSION'."
  git tag -a "$TAG" -m "Release $VERSION"
  git push origin "$TAG"
  gh release create "$TAG" --title "$VERSION" --generate-notes --verify-tag
fi

if should_run back-merge; then
  info "[back-merge] Merging master into develop via $BACKMERGE_BRANCH"
  ensure_label
  git checkout develop
  git pull --ff-only origin develop
  if ! git show-ref --quiet "refs/heads/$BACKMERGE_BRANCH"; then
    git checkout -b "$BACKMERGE_BRANCH"
    git merge --no-ff origin/master -m "Merge master into develop after release $VERSION"
  else
    git checkout "$BACKMERGE_BRANCH"
  fi
  git push -u origin "$BACKMERGE_BRANCH"
  if ! gh pr view "$BACKMERGE_BRANCH" --json state -q .state 2>/dev/null | grep -qE 'OPEN|MERGED'; then
    gh pr create --base develop --head "$BACKMERGE_BRANCH" --label "$SKIP_LABEL" \
      --title "Merge master into develop after release $VERSION" \
      --body "Back-merge after release $VERSION. **Merge with a merge commit** (not squash/rebase)."
  fi
  echo "Reminder: use a MERGE COMMIT, not squash or rebase."
  wait_for_merge "$BACKMERGE_BRANCH" back-merge
  git fetch --quiet origin
  git merge-base --is-ancestor origin/master origin/develop \
    || die "origin/master is not an ancestor of origin/develop — was the PR squashed or rebased?"
fi

if should_run bump; then
  if [[ -z "$NEXT_VERSION" ]]; then
    while true; do
      read -r -p "Next development version (X.Y.Z, -SNAPSHOT is appended): " NEXT_VERSION
      valid_version "$NEXT_VERSION" && break
      echo "Invalid version '$NEXT_VERSION'."
    done
  fi
  SNAPSHOT="$NEXT_VERSION-SNAPSHOT"
  info "[bump] Bumping develop to $SNAPSHOT"
  git checkout develop
  git pull --ff-only origin develop
  set_revision "$SNAPSHOT"
  git commit -am "Bump to $SNAPSHOT"
  git push origin develop
fi

echo
echo "Release $VERSION done."
echo "Reminder: check the deploy workflow and run the post-deploy checks in docs/RELEASING.md."
