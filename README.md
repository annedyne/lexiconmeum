# LexiconMeum Backend ![Status](https://img.shields.io/badge/status-in--development-yellow)
> 🌐 Live at: [https://lexicon.annedyne.net](https://lexicon.annedyne.net)

This is the backend service for **LexiconMeum**, a Latin vocabulary search and grammar tool.  
It provides a RESTful API for querying word definitions, grammatical forms, and lexical data.

Built with **Spring Boot**, it loads pre-parsed data from Wiktionary and serves search results in real time.  
Designed for fast lookup, flexible querying, and integration with a React-based frontend.

Lexical data is sourced from [Wiktionary](https://www.wiktionary.org/) and parsed into a searchable format.


## Features

- REST API for querying Latin words by prefix or suffix
- Declension/conjugation info per lexeme
- JSONL-based data loader 
- CORS-enabled for frontend integration

### 🔄 API Contract

OpenAPI docs are generated at runtime:

```bash
GET /v3/api-docs
GET /swagger-ui/index.html
```

##### word search endpoint:

given prefix (match beginning of word)
```bash
GET /api/v1/lexemes/autocomplete/prefix?prefix=<string>&limit=<integer>

Response: JSON array of suggestion objects.
```
given suffix (match end of word)

```bash
GET /api/v1/lexemes/autocomplete/suffix?suffix=<string>&limit=<integer>

Response: JSON array of suggestion objects.
```
##### word detail endpoints
```bash
GET /api/v1/lexemes/{id}/detail?type=<string>  # type is optional
GET /api/v1/lexemes?lexemeId=<string>

Response: JSON object.
```

## Versioning and Deployment

1. Create a release branch from develop
```bash
# Replace 0.1.1 with the actual current version
git checkout develop
git checkout -b release/0.1.1
```
2. Bump the version to a non-SNAPSHOT
```bash
# Replace 0.1.1 with the actual current version
mvn versions:set -DnewVersion=0.1.1
git commit -am "Prepare release 0.1.1"
```
3. Open a PR from release branch → master

   ( Will trigger deploy from master. )
    - Title: Release 0.1.1
    - Description:  Summary, deployment notes, testing status

4. tag after merge
```bash
# Replace 0.1.1 with the actual current version
git checkout master
git pull origin master
git tag -a v0.1.1 -m "Release 0.1.1"
git push origin v0.1.1
```
5. Bump develop for next snapshot
```bash
# Replace 0.1.1 with the actual current version
git checkout develop
mvn versions:set -DnewVersion=0.1.2-SNAPSHOT
git commit -am "Bump to 0.1.2-SNAPSHOT"
git push origin develop
```

## Stack
### Backend:
- REST APIs: Java SpringBoot Server
- LEXICAL DATA:  jsonl file 
- CACHE: Caffeine

### Front-End:
- Current: Javascript Future: React 

## Data Source

Lexical data originate from [Wiktionary](https://www.wiktionary.org/) (licensed under [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/)) and are obtained in machine‑readable form via [Kaikki.org](https://kaikki.org/) using the Wiktextract project.  
If you cite this dataset or derivative work, please also credit Wiktextract as requested by Kaikki.org (e.g., “Tatu Ylonen: Wiktextract: Wiktionary as Machine‑Readable Structured Data, LREC”) and provide links to the sources and license.  
LexiconMeum is not affiliated with or endorsed by Wiktionary, the Wikimedia Foundation, or Kaikki.org.
