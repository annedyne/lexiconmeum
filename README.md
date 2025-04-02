# MeaLexica App

## MVP Functional Requirements
- User should be able to search for a word and get a real-time drop down of suggestions (autocompleted) based on what they’ve typed so far.
- User should be able to view a detail of the word showing it’s definition and basic tenses in tables

Phase II
- User should be able to click on tabs to view participles, passive
- User should be able to click on tabs to view masculine, feminine or neuter versions of the above

Phase III
- User should be able to search for words using tags.
- User should be able to search for words using English equivalents

Phase IV
- containerize

Scale 
- 1G worth of words

## Non Functional Requirements
- low latency search - < 200 ms
- real time drop-down 


## Core Entities
- lemma
- definition
- inflection
- tense
- gender
- voice
- aspect

## APIS
- GET /lemma/:id -> Detail


## Stack
### Backend:
- REST APIs: Java SpringBoot Server
- DB: Postgres
- CACHE: Caffiene

### Front-End:
- React