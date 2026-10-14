# LexiconMeum Backend

Java/Spring backend for a Latin lexical search and grammar application.

> Live demo: [https://lexicon.annedyne.net](https://lexicon.annedyne.net)

## Overview

LexiconMeum ingests machine-readable Latin lexical data derived from Wiktionary, normalizes it into a typed domain model, and serves search and lexeme detail APIs for a frontend client.

The project focuses on a domain that is structurally irregular in the source data but highly structured in the user-facing result: dictionary entries, grammatical features, inflections, and related lexical forms.

The current version represents a working MVP, with ongoing development focused on feature completeness, performance, and operational improvements.

## What it does

The backend supports two main flows:

- **Autocomplete search** for Latin words by prefix or suffix
- **Lexeme detail lookup** with definitions, grammatical metadata, and inflection-oriented response sections

At runtime, the service loads pre-parsed lexical data, builds searchable forms, and exposes a REST API for frontend use.

## Engineering Highlights

- Custom ingestion pipeline for Wiktionary/Kaikki JSONL lexical data
- Typed grammar and lexical domain model for morphology-heavy data
- Staging and linking flow for relationships that are not fully resolved in a single source record
- Prefix and suffix search support over indexed lexical forms
- Frontend-oriented response assembly for lexeme detail endpoints
- OpenAPI documentation for API discovery
- Test coverage across parsing, mapping, search, and response assembly

## Architecture

At a high level, the backend flows like this:

```text
text Wiktionary/Kaikki JSONL 
        |
        v
ingest parsers + tag mapping 
        |
        v
staging and linking 
        |
        v
typed Lexeme domain model 
        |
        v
in-memory cache + search indexes 
        |
        v
REST API / frontend-oriented responses
```

The architecture separates source-data ingestion from API response assembly:

- the ingest layer interprets raw lexical records
- the shared model captures typed grammar and lexical structure
- the search layer builds lookup-oriented indexes
- the web layer assembles stable responses for the frontend

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for a more detailed walkthrough.

## Deployment and operations

The app runs as a systemd service behind nginx on a single VPS. GitHub Actions builds and deploys it.

In place:

- **Crash recovery and self-healing:** systemd restarts the process if it dies. An external watchdog polls `/actuator/health` and restarts the app if it stays unhealthy.
- **Graceful shutdown:** on stop, in-flight requests get up to 30s to finish.
- **Capacity planning:** the JVM heap is capped and swap is configured, leaving guaranteed headroom for other workloads.
- **Metrics:** Micrometer exposes Prometheus metrics on a separate management port. Its bind address is set per environment.
- **k3s:** a single-node cluster runs alongside the app. Kubelet reservations and eviction thresholds stop cluster workloads from starving the app.

In progress:

- Prometheus and Grafana on k3s, installed via Helm from declarative config. Prometheus scrapes the app as an out-of-cluster target.
- Alerting on app health, error rates, and memory pressure.

Ops configuration lives in a separate infrastructure repository. The app repo stays environment-agnostic, and the stack can be reinstalled on a new host from version-controlled config.

## Run locally

### Prerequisites

- Java 21
- Maven 3.9+ or the included Maven wrapper

### Start the application
```bash
./mvnw spring-boot:run
```
Or build and run the JAR:
```bash
./mvnw clean package java -jar target/lexiconmeum-0.12.0-SNAPSHOT.jar
```
### Run tests
```bash
./mvnw test
```

## API

OpenAPI docs are generated at runtime:
```bash
GET  /api/v1/api-docs 
GET  /api/v1/swagger-ui/index.html
```
### Autocomplete
```bash
GET /api/v1/lexemes/autocomplete/prefix?prefix= &limit= 
GET /api/v1/lexemes/autocomplete/suffix?suffix= &limit=
```
### Lexeme detail
```bash
GET /api/v1/lexemes/{id}/detail?type= 
GET /api/v1/lexemes?lexemeId=
```

## Documentation

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — system structure, data flow, and design tradeoffs
- [docs/RELEASING.md](docs/RELEASING.md) — release, versioning, and deployment workflow
- [ROADMAP.md](ROADMAP.md) — planned work and project direction

## Data source and attribution

Lexical data originates from [Wiktionary](https://www.wiktionary.org/) and is consumed in machine-readable form via [Kaikki.org](https://kaikki.org/) and the Wiktextract project.

Relevant attribution and licensing notes:

- Wiktionary content is licensed under [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/)
- Kaikki requests attribution to Wiktextract, for example:  
  *Tatu Ylonen: Wiktextract: Wiktionary as Machine-Readable Structured Data, LREC*
- LexiconMeum is not affiliated with or endorsed by Wiktionary, the Wikimedia Foundation, or Kaikki.org.


