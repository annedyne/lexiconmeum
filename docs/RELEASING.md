# Releasing and Deployment

This document contains the maintainer workflow for versioning, releasing, and deploying LexiconMeum Backend.

## Deployment overview

Production deployment is currently handled by GitHub Actions on pushes to `master`.

Workflow file:

- `.github/workflows/deploy.yml`

The deploy workflow currently:

1. builds the application JAR with Maven
2. uploads the artifact to the VPS
3. moves the JAR into the application directory
4. updates the `lexiconmeum.jar` symlink
5. restarts the systemd service

## Branching model

The current release flow is:

- feature and integration work on `develop`
- release preparation on `release/<version>`
- production deploys from `master`

If the branching strategy changes, update this document alongside the workflow configuration.

## Versioning

Project version is defined in `pom.xml`.

Release preparation uses a non-SNAPSHOT version. Ongoing development on `develop` should move to the next `-SNAPSHOT` version after release.

The patch (third) number is reserved for hotfixes. So after releasing `X.Y.0`, the develop bump is a minor bump to `X.(Y+1).0-SNAPSHOT` (for example, release `0.14.0` is followed by `0.15.0-SNAPSHOT`), not `X.Y.1-SNAPSHOT`.

## Release checklist

### 1. Sync `develop`
```bash 
git checkout develop git pull origin develop
```

### 2. Create the release branch
```bash
git checkout -b release/0.12.0 git push origin release/0.12.0
```

### 3. Set the release version
```bash 
mvn versions:set -DnewVersion=0.12.0 git commit -am "Prepare release 0.12.0" git push origin release/0.12.0
```

### 4. Open the release PR

Open a pull request from:
```bash 
release/0.12.0 -> master
```

Recommended PR content:

- summary of changes
- testing status
- deployment notes
- known issues, if any

### 5. Merge and tag the release

After the PR is merged:
bash git checkout master git pull origin master git tag -a v0.12.0 -m "Release 0.12.0" git push origin v0.12.0


### 6. Bump `develop` to the next snapshot

Use the next minor snapshot (patch is reserved for hotfixes). After a `0.12.0` release:
```bash
git checkout develop
git pull origin develop
mvn versions:set -DnewVersion=0.13.0-SNAPSHOT
git commit -am "Bump to 0.13.0-SNAPSHOT"
git push origin develop
```

This SNAPSHOT bump is the one sanctioned direct push to `develop`; every other change reaches `develop` and `master` through pull requests.

## Deployment details

The GitHub Actions workflow currently deploys to a VPS over SSH.

Key details reflected in `.github/workflows/deploy.yml`:

- trigger: push to `master`
- build command: `mvn -B clean package -DskipTests`
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
- If deployment moves away from `master`, revise both this file and `.github/workflows/deploy.yml`

