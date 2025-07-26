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

##### word search endpoint:

given prefix (match beginning of word)
```bash
GET /api/v1/autocomplete/prefix?prefix=<string>

Response: JSON array of matching words (e.g.,
["amare", "amatus"])
```
given suffix (match end of word)

```bash
GET /api/v1/autocomplete/suffix?suffix=<string>
Response: JSON array of matching words (e.g.,
["amaturus", "amonibus, "]
```
##### word detail endpoints
```bash
GET /api/v1/lexemes/123/detail?type=<string>  #type is optional
GET /api/v1/lexemes?lexemeId=<string>
Response: JSON object (e.g.,
["amare", "amatus"])
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
git commit -am "Start 0.1.2-SNAPSHOT"
git push origin develop
```

## MVP Functional Requirements
- [X] User should be able to search for a lexeme and get a real-time drop down of suggestions (autocompleted) based on what they’ve typed so far.
- User should be able to view a detail of the lexeme showing it’s
    - definition
    - [X] basic verb tenses in tables
    - [X] noun declensions in tables

Phase II
- User should be able to click on tabs to view participles, passive
- User should be able to click on tabs to view masculine, feminine or neuter versions of the above

Phase III
- User should be able to search for lexemes using tags.
- User should be able to search for lexemes using English equivalents

Phase IV
- containerize

Scale 
- 1G of lexical data

## Non Functional Requirements
- low latency search - < 200 ms
- real time drop-down 


## Core Entities
- Lexeme 
    - position
    - glosses
    - inflections
        - form
- Grammatical Features (can be associated at Lexeme or Inflection Level)
    - position 
    - tense
    - gender
    - voice
    - aspect
- Lexeme Detail
    - definition
    - Inflection table

## Stack
### Backend:
- REST APIs: Java SpringBoot Server
- LEXICAL DATA:  jsonl file 
- CACHE: Caffeine

### Front-End:
- Current: Javascript Future: React 

## Data Source

This project uses data extracted from [Wiktionary](https://www.wiktionary.org/),  
which is available under the [Creative Commons Attribution-ShareAlike 3.0 Unported License (CC BY-SA 3.0)](https://creativecommons.org/licenses/by-sa/3.0/).  
LexiconMeum is not affiliated with or endorsed by Wiktionary or the Wikimedia Foundation.
