# Test Resource Fixtures

These resources are intentionally small samples of Wiktionary/Kaikki-style data
and Spring test configuration. Prefer adding the narrowest fixture record needed
for a behavior change instead of broad fixture churn.

## JSONL Lexical Data

Each `.jsonl` file contains one lexical JSON object per line. Parser tests load
these files through `ClassPathResource` or `JsonTestDataManager`.

| File | Scope | Notable entries | Common test uses |
| --- | --- | --- | --- |
| `testDataRaw.jsonl` | Mixed end-to-end parser sample with verbs, nouns, adjectives, pronouns, determiners, conjunctions, participles, duplicate lemmas, and invalid/non-lemma cases. | `sum`, `amo`, `poculum`, `pulcher`, `brevis`, `nox`, `etsi`, `pulso`, `ille`, `sequor`, plus participles such as `futurus`, `doctus`, `amans`, `sequendus`, and `amandus`. | Broad parser smoke tests, staging vs. consumed lexeme behavior, valid lemma filtering, duplicate lemma handling, participle parent lookup, and POS routing coverage. |
| `testDataVerb.jsonl` | Verb-centered fixture for parsed verb lexemes and verb-like participle/noun collisions. | `sum`, `amo`, `sequor`, `amans` as both verb participle and noun, plus pronoun `sum`. | Verb parsing, conjugation DTO assembly, compound deponent forms, autocomplete/index tests involving verbs, and present active participle parsing. |
| `testDataNoun.jsonl` | Noun-centered fixture with declension coverage and noun/adjective collisions. | `poculum`, `brevis` masculine/feminine noun entries, `nox`, and `amicus` as both adjective and noun. | Noun parsing, declension table mapping, autocomplete/index tests involving nouns, and same-lemma POS disambiguation. |
| `testDataAdjective.jsonl` | Adjective fixture with positive, comparative, and superlative forms. | `pulcher`/`pulchrior`/`pulcherrimus`, `brevis`/`brevissimus`, `levis`/`levior`/`levissimus`, and participial adjective data such as `doctus`. | Adjective parser tests, degree handling, comparative/superlative routing, and adjective/noun ambiguity checks. |
| `testDataquisqui.jsonl` | Focused `quis` fixture covering multiple POS interpretations. | `quis` as pronoun, determiner, and verb-like head entry. | Narrow POS key and pronoun/determiner parser scenarios. |

When adding a valid lemma to `testDataRaw.jsonl`, also check the expected count
lists in `WiktionaryLexicalDataParserTest`.

## Structured JSON

| File | Scope | Common test uses |
| --- | --- | --- |
| `participles.json` | Hand-built participle table fixture grouped by voice/tense keys: `ACTIVE|PRESENT`, `ACTIVE|FUTURE`, `PASSIVE|PERFECT`, and `PASSIVE|FUTURE`. | `ParticipleTableMapperTest` builds a synthetic `amo` verb lexeme with participle declension sets and verifies DTO table coverage by gender and tense. |

## Spring Test Configuration

| File | Scope | Common test uses |
| --- | --- | --- |
| `application-test.yml` | Test profile properties. Sets `test.base-url` and points application data loading at `classpath:lexicalDataPartial.jsonl`. | Spring Boot integration tests and controller tests that need predictable test profile settings. |
