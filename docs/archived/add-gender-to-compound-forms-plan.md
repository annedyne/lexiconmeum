# Add feminine and neuter forms of compound verb tenses

## Requirement
Back end should provide ALL gender forms of the passive perfect tenses of regular verbs and the active perfect tenses of deponent verbs.
Ex: first person perfect passive of amo: amātus sum, amāta sum, amātum sum.

## Root cause
Compound forms are built at parse time in POSVerbParser from the main-verb jsonl, whose form node carries only the masculine participle lemma. The feminine/neuter nominatives live in the participle POS jsonl and only reach the verb (as participle declension sets on VerbDetails) after linking completes. The two can only be joined post-link.

## Decision: generate post-link, not on demand
Keeps the existing "precompute typed data, response layer just bundles" paradigm; gender is a property of the form and belongs in the domain model, computed once at startup. On-demand generation would push grammar synthesis into the presentation layer and would still leave masculine at parse time (a worse split).

## Approach
Model change is the prerequisite: compound forms need a gender dimension, because the three gendered forms currently collapse to one conjugation key.

- **Conjugation (shared/model/inflection)**: add optional grammatical gender. Non-null only for compound/participial tenses, null for simple tenses. Include it in the conjugation inflection key (the key builder already omits null parts).

- **New CompoundInflectionGenerator (ingest/wiktionary)**: extract the esse-combination + tense iteration currently inline in POSVerbParser. Given a participle set and its gendered nominative forms, produce the gendered compound conjugations. Shared source of truth for compound generation.

- **POSVerbParser**: keep parse-time masculine generation as a fallback baseline so verbs whose participle POS entry never links do not lose their compound forms. Delegate the esse/tense logic to the new generator.

- **DataLinkingService.finalizeLexicalDataLinking**: after participles are attached, enrich each verb. For each perfect participle set present ((PASSIVE,PERFECT) for regular, (ACTIVE,PERFECT) for deponent via MorphologicalSubtype), pull its nominative singular and plural forms per gender and add the corresponding gendered compound conjugations. Which compound tenses exist is derivable from which participle sets are present, so no dependence on the raw jsonl here.

## Notes
- Retrieve gendered nominatives by iterating the participle set's inflections and filtering on case NOMINATIVE + number + genders-contains-gender. Do not reconstruct keys: participle keys use a gender Set.
- Deponent detection is available on the linked verb via VerbDetails morphological subtype.
- Masculine forms are also compound forms; routing all gender generation through the one generator keeps them consistent.

## Testing
- Conjugation key includes gender: unit test in shared/model.
- CompoundInflectionGenerator produces three genders sg + pl for a sample participle set: unit test.
- End-to-end: a regular verb (amo) and a deponent yield feminine/neuter perfect forms after finalization.