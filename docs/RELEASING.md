# Releasing and Deployment

This document contains the maintainer workflow for versioning, releasing, and deploying LexiconMeum Backend.

## Deployment overview

Production deployment is handled by GitHub Actions when a `v*` release tag is pushed. A manual
`workflow_dispatch` run is also available and takes the version to build as an input.

Workflow file:

- `.github/workflows/deploy.yml`

The deploy workflow:

1. verifies the tagged commit is on `master`
2. derives the version from the tag name (or the manual input)
3. builds the application JAR with Maven
4. uploads the artifact to the VPS
5. moves the JAR into the application directory
6. updates the `lexiconmeum.jar` symlink
7. restarts the systemd service

## Branching model

The current release flow is:

- feature and integration work on `develop`
- release preparation on `release/<version>`
- production deploys from `v*` tags on `master`

If the branching strategy changes, update this document alongside the workflow configuration.

## Versioning

Project version is defined by the `revision` property in `pom.xml`; `<version>` is `${revision}`.
This lets the deploy workflow build a given version with `-Drevision=<version>` without editing the pom.

Release preparation uses a non-SNAPSHOT version. Ongoing development on `develop` should move to the next `-SNAPSHOT` version after release.

The patch (third) number is reserved for hotfixes. So after releasing `X.Y.0`, the develop bump is a minor bump to `X.(Y+1).0-SNAPSHOT` (for example, release `0.1.0` is followed by `0.2.0-SNAPSHOT`), not `X.Y.1-SNAPSHOT`.

## Release script

`scripts/release.sh` automates the checklist below (also available as `make release`):

```bash
scripts/release.sh [--from STEP] [NEXT_VERSION]
make release [NEXT=0.22.0] [FROM=tag]
```

- The release version is always the `revision` in `origin/develop`'s `pom.xml`, minus `-SNAPSHOT`.
- `NEXT_VERSION` is the next development version as `X.Y.Z`; the script appends `-SNAPSHOT`. If omitted, it prompts for it at the bump step.
- Steps, in order: `branch` → `release-pr` → `tag` → `back-merge` → `bump` (checklist steps 2–6).
- At each PR it opens (via `gh`), it pauses with a y/n prompt until you have merged it. Answering `n` or a failure exits with the `--from` command to resume.
- Before making changes it checks for a clean working tree, an authenticated `gh`, a `-SNAPSHOT` revision, and that the release branch and tag do not already exist.
- The release PR body comes from GitHub's generated release notes since the previous tag; after tagging, it creates a GitHub Release with `--generate-notes`.
- Release and back-merge PRs are labelled `skip-changelog`, which `.github/release.yml` excludes from generated notes.
- After the back-merge PR merges, it verifies `master` is an ancestor of `develop` (catches squash/rebase merges).
- It does not watch the deploy workflow; run the [post-deploy checks](#post-deploy-checks) yourself.

The manual checklist below documents what the script does and is the fallback if it cannot be used.

## Release checklist

### 1. Sync `develop`
```bash
git checkout develop
git pull origin develop
```

### 2. Create the release branch and set the release version
```bash
git checkout -b release/0.1.0
mvn versions:set-property -Dproperty=revision -DnewVersion=0.1.0
git commit -am "Prepare release 0.1.0"
git push -u origin release/0.1.0
```

### 3. Open the release PR

Open a pull request from:
```bash 
release/0.1.0 -> master
```

Recommended PR content:

- summary of changes
- testing status
- deployment notes
- known issues, if any

### 4. Merge and tag the release

After the PR is merged. Pushing the tag triggers the deploy workflow:
```bash
git checkout master
git pull origin master
git tag -a v0.1.0 -m "Release 0.1.0"
git push origin v0.1.0
```

### 5. Back-merge `master` into `develop`

Bring the release commits back into `develop` so the next release PR doesn't conflict on `<revision>`:
```bash
git checkout develop
git pull origin develop
git checkout -b chore/back-merge-master-0.1.0
git merge --no-ff origin/master -m "Merge master into develop after release 0.1.0"
git push -u origin chore/back-merge-master-0.1.0
```

Open a PR to `develop` and merge it with a **merge commit** (not squash or rebase), or the ancestry is lost and the conflict returns.

### 6. Bump `develop` to the next snapshot

Use the next minor snapshot (patch is reserved for hotfixes). After a `0.1.0` release:
```bash
git checkout develop
git pull origin develop
mvn versions:set-property -Dproperty=revision -DnewVersion=0.2.0-SNAPSHOT
git commit -am "Bump to 0.2.0-SNAPSHOT"
git push origin develop
```

This SNAPSHOT bump is the one sanctioned direct push to `develop`; every other change reaches `develop` and `master` through pull requests.

## Deployment details

The GitHub Actions workflow currently deploys to a VPS over SSH.

Key details reflected in `.github/workflows/deploy.yml`:

- trigger: push of a `v*` tag, or a manual run with a `revision` input
- build command: `mvn -B clean package -DskipTests -Drevision=<version>`
- SSH key source: `DEPLOY_KEY`
- target host secret: `VPS_IP_PROD`
- remote application directory: `/opt/lexiconmeum`

The remote deployment step:

- uploads the built JAR to the server
- moves it into the application directory
- updates the `lexiconmeum.jar` symlink
- restarts `lexiconmeum.service`

## Post-deploy checks

After deployment, verify:

1. the GitHub Actions workflow completed successfully
2. the application started successfully on the VPS
3. the public endpoint is reachable
4. the OpenAPI endpoints load
5. a basic autocomplete request succeeds

Useful checks:
```bash 
GET /api/v1/swagger-ui/index.html 
GET /api/v1/api-docs 
GET /api/v1/lexemes/autocomplete/prefix?prefix=am&limit=5
```

## Maintenance notes

- Keep release and deployment instructions here rather than in `README.md`
- Update this document when branch names, server paths, or deployment steps change
- Keep `scripts/release.sh` in sync with the release checklist
- If deployment moves away from `v*` tags on `master`, revise both this file and `.github/workflows/deploy.yml`