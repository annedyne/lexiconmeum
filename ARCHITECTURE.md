# LexiconMeum Architecture

This document summarizes the backend structure for contributors and coding agents.

## Overview

LexiconMeum is a Spring Boot service that provides Latin lexical lookup APIs. The backend ingests machine-readable Wiktionary/Kaikki lexical data, maps it into domain objects, stores lexemes in an in-memory cache, builds text-search indexes, and serves frontend-oriented REST responses.

At a high level:

```text
Wiktionary/Kaikki JSONL
        |
        v
ingest/wiktionary parsers and staging
        |
        v
tagmapping factories
        |
        v
shared Lexeme domain model
        |
        v
in-memory cache and search indexes
        |
        v
webapi BFF controllers and response assemblers
```

## Runtime Data Flow

The application loads bundled lexical data from `src/main/resources/lexicalDataPartial.jsonl`. Ingest components parse raw entries into lexeme objects, normalize grammatical features, and link related lexical records where needed. The resulting lexemes are stored behind shared reader/sink abstractions and served through the web API.

Text search builds searchable forms from lexemes, including principal forms and inflected forms where supported. Autocomplete requests query an index implementation and map matched lexemes into suggestion responses.

Lexeme detail requests retrieve a lexeme by id and assemble a frontend-oriented response from section contributors. Each contributor owns one piece of the response, such as definitions, principal parts, gender, subtype, inflection class, or inflection tables.

## Fast Lookup

- Parser behavior: `src/main/java/com/annepolis/lexiconmeum/ingest/wiktionary`
- Raw tag mapping: `src/main/java/com/annepolis/lexiconmeum/ingest/tagmapping`
- Domain grammar/model: `src/main/java/com/annepolis/lexiconmeum/shared/model`
- Runtime lexeme cache: `src/main/java/com/annepolis/lexiconmeum/cache/inmemory`
- Autocomplete/text search: `src/main/java/com/annepolis/lexiconmeum/webapi/bff/textsearch`
- Lexeme detail response: `src/main/java/com/annepolis/lexiconmeum/webapi/bff/lexemedetail`
- Routes, response keys, CORS, properties: `src/main/java/com/annepolis/lexiconmeum/webapi`
- Runtime data: `src/main/resources/lexicalDataPartial.jsonl`
- Test fixtures: `src/test/resources`

## Main Modules

### Ingest

`src/main/java/com/annepolis/lexiconmeum/ingest/wiktionary` contains parsing and staging logic for raw Wiktionary/Kaikki data. Part-of-speech-specific parser classes handle nouns, verbs, adjectives, participles, conjunctions, and non-inflected forms.

`src/main/java/com/annepolis/lexiconmeum/ingest/tagmapping` maps raw tags into typed domain concepts such as part-of-speech details, inflection classes, and inflection features.

Parser changes should stay close to the relevant parser or factory unless the same rule is reused across multiple parts of speech.

### Shared Domain

`src/main/java/com/annepolis/lexiconmeum/shared/model` contains the core model:

- `Lexeme`, `Sense`, and builders.
- Inflection objects and inflection keys.
- Grammar enums for case, number, gender, person, tense, mood, voice, degree, and related concepts.
- Part-of-speech detail types.

The shared model should remain independent of controller response shapes. Prefer typed grammar concepts here instead of raw strings.

### Cache

`src/main/java/com/annepolis/lexiconmeum/cache/inmemory` contains the in-memory cache implementation. The cache is the runtime lookup layer between loaded lexical data and API use cases.

### Text Search BFF

`src/main/java/com/annepolis/lexiconmeum/webapi/bff/textsearch` owns autocomplete behavior.

Important responsibilities:

- Extract searchable forms from lexemes.
- Route lexemes to the appropriate form extractor.
- Build and query autocomplete indexes.
- Map matched entries into suggestion responses.
- Serve text-search endpoints through `TextSearchController`.

The search layer should handle frontend search needs without leaking parsing-stage details.

### Lexeme Detail BFF

`src/main/java/com/annepolis/lexiconmeum/webapi/bff/lexemedetail` owns lexeme detail responses.

Important responsibilities:

- Retrieve the requested lexeme.
- Select and assemble response sections.
- Convert domain inflections into table-oriented DTOs.
- Keep response assembly modular through `LexemeDetailSectionContributor` implementations.

When adding a new detail field, look first for an existing section contributor that owns that concept. Add a new contributor only when the response section is distinct enough to deserve its own assembly path.

### Web API

`src/main/java/com/annepolis/lexiconmeum/webapi` contains route constants, API response keys, CORS configuration, and web properties.

Keep route paths and shared response-key literals centralized here.

## Configuration

Runtime configuration lives in `src/main/resources`:

- Default: `application.yml`
- Local profile: `application-local.yml`
- Development profile: `application-dev.yml`
- Logging: `log4j2.properties`, `log4j2-dev.properties`

Tests use `src/test/resources/application-test.yml`.

## Testing Strategy

Tests are organized near the behavior they cover:

- Parser: `src/test/java/com/annepolis/lexiconmeum/ingest/wiktionary`
- Tag mapping: `src/test/java/com/annepolis/lexiconmeum/ingest/tagmapping` and related grammar tests
- Domain model: `src/test/java/com/annepolis/lexiconmeum/shared/model`
- Text search: `src/test/java/com/annepolis/lexiconmeum/webapi/bff/textsearch`
- Lexeme detail and inflection tables: `src/test/java/com/annepolis/lexiconmeum/webapi/bff/lexemedetail`

Use focused fixtures from `src/test/resources` when possible. Keep fixture edits small and targeted; do not rewrite JSONL data unless the task is specifically about data regeneration.

## Deployment

CI runs Maven tests on pull requests and pushes to `develop` and `master`. Deployment is handled by the GitHub Actions workflow for pushes to `master`, which builds the JAR and deploys it to the VPS service.

See `README.md` for release branch and versioning steps.

## Design Constraints

- Keep ingest/parsing concerns separate from BFF response assembly.
- Keep domain grammar concepts typed.
- Never rewrite JSONL data unless the task is specifically about data regeneration.
- Favor small, package-local changes over cross-cutting abstractions.
- Keep API response changes intentional because the frontend depends on these shapes.
