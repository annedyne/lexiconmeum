# LexiconMeum App

## Deployment


### 🔄 API Contract

##### word search endpoint:
* given prefix
  - `GET /api/search/prefix?prefix=<string>`
    Response: JSON array of matching words (e.g., `["amare", "amatus"]`)
* given suffix
  - `GET /api/search/suffix?suffix=<string>`
    Response: JSON array of matching words (e.g., `["amaturus", "amonibus, "]`)

GET /api/search/prefix?prefix=
"http://localhost:8080/api/v1"

## MVP Functional Requirements
- User should be able to search for a lexeme and get a real-time drop down of suggestions (autocompleted) based on what they’ve typed so far.
- User should be able to view a detail of the lexeme showing it’s definition and basic tenses in tables

Phase II
- User should be able to click on tabs to view participles, passive
- User should be able to click on tabs to view masculine, feminine or neuter versions of the above

Phase III
- User should be able to search for lexemes using tags.
- User should be able to search for lexemes using English equivalents

Phase IV
- containerize

Scale 
- 1G worth of lexemes

## Non Functional Requirements
- low latency search - < 200 ms
- real time drop-down 


## Core Entities
- lemmaDetail
- definition
- lexeme
    - lexeme (idx)
    - tense
    - gender
    - voice
    - aspect

## APIS
get all matching lexemes with input text
- GET /lemmas/lexemes?text={text} -> Word[]

- get lemmas associated with inflected lexemes
- GET /lemmas?term={inflectedForm} -> Partial<Lemma>

- Get all inflected forms associated with a given lexeme
- GET /lemmas/:id -> LemmaDetail


add lemmas associated with matching lexemes

## Stack
### Backend:
- REST APIs: Java SpringBoot Server
- DB: Postgres
- CACHE: Caffeine

### Front-End:
- React