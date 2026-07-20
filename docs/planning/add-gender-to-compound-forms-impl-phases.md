# Phased implementation: gender on compound verb forms

Companion to add-gender-to-compound-forms-plan.md. Each phase is independently testable and leaves the build green.

## Phase 1 - Model: gender on Conjugation
Add the gender dimension without changing any generation yet.

- **Conjugation (shared/model/inflection)**: add optional grammatical gender field, builder setter, getter, carry through toBuilder.
- **InflectionKey**: include gender in the conjugation key; null gender omits the part (existing null-part behavior).

Test: gender defaults null and simple-tense keys are unchanged; two conjugations differing only by gender produce distinct keys.

## Phase 2 - Extract CompoundInflectionGenerator
Pure refactor, no behavior change. Masculine-only output stays identical.

- **New CompoundInflectionGenerator (ingest/wiktionary)**: move the esse-combination + number/person iteration out of POSVerbParser.addCompoundInflectionForms. Takes a participle base form plus voice/mood/tense; returns the compound conjugations for one gender.
- **POSVerbParser**: delegate to the generator; behavior identical.

Test: existing verb-parser tests pass unchanged.

## Phase 3 - Generator produces all genders from a participle set
Add the gendered capability to the generator, still not wired into linking.

- **CompoundInflectionGenerator**: add an entry point taking a participle declension set + target voice/mood/tense; retrieve nominative singular and plural per gender (filter inflections on case NOMINATIVE + number + genders-contains-gender), emit gendered compound conjugations with gender set.

Test: unit test with a sample perfect participle set yields masc/fem/neut for sg and pl with correct forms and keys.

## Phase 4 - Wire post-link enrichment
Generate gendered compound forms during finalization.

- **DataLinkingService.finalizeLexicalDataLinking**: after participles are attached, for each verb enrich from its perfect participle set - (PASSIVE,PERFECT) for regular, (ACTIVE,PERFECT) for deponent via morphological subtype - adding the gendered compound conjugations across the perfect/pluperfect/future-perfect indicative + subjunctive tenses.
- **POSVerbParser**: keep parse-time masculine generation as fallback for verbs whose participle set never links.

Test: end-to-end - amo (regular) and a deponent expose feminine/neuter perfect forms after finalization; a verb with no linked participle still has masculine forms.

## Phase 5 - Surface and verify
Confirm the new forms reach the API and de-duplicate against the fallback.

- **webapi/bff/lexemedetail**: verify gendered forms appear in the detail response; ensure masculine fallback and post-link masculine do not double-add (merge on key).

Test: detail-response coverage for a perfect passive tense showing all three genders.

## Phase 6 - Partition compound forms by gender within each tense
Today all genders are jumbled into one tense's flat forms list (compoundTenseCurrent.json), so the front-end cannot triage by gender. Keep the existing table and tense structure and ordering unchanged; a compound tense exposes its forms bucketed by gender via a separate field. The front-end reads the active gender's list per tense. Non-gendered (simple) tenses keep their flat list.

Decision: use two optional fields on the tense (`forms` for simple tenses, `formsByGender` for compound), rather than making the single `forms` field polymorphic, duplicating whole tense lists per gender, or keying tenses by name.
- Preserves the curated tense order and adds no duplication of non-gendered forms.
- Both fields stay strictly typed, so the OpenAPI schema and generated clients keep their guarantees (a polymorphic `forms` would degrade to a loose `object`/`oneOf`).
- The front-end does a presence check and renders `formsByGender[activeGender]` directly - the server has already bucketed - instead of type-sniffing or grouping client-side.
- Rejected: whole per-gender conjugation lists (triplicates non-gendered tenses and misattributes them to a gender) and tense-keyed maps (front-end must know subjective tense keys; object key order is not a reliable ordering channel).

- **ConjugationTableDTO.TenseDTO (webapi/bff/lexemedetail/dtoassembly/inflection)**: add a `formsByGender` field (per-gender map of ordered form lists) beside the existing `forms`; only one is populated per tense. Tense identity, order, and names are untouched.
- **ConjugationTableMapper**: when a tense's forms carry a gender, bucket them by gender - each gender's list ordered by number then person, genders in natural order - and set `formsByGender`; otherwise set the flat `forms` as today. Drop the Phase 5 gender interleave sort, which the per-gender bucketing supersedes.

Notes:
- All linked verbs have gendered compound tenses, so a compound tense always emits `formsByGender` with all three genders. The flat `forms` path remains for simple tenses (and any unlinked-participle fallback).
- ConjugationGroupDTO keeps its existing `conjugations` / `participles` shape; no new top-level section.

Test: a compound tense serializes `formsByGender` (gender-keyed, each a clean single-gender paradigm in number/person order) and no flat `forms`; a simple tense still serializes flat `forms` and no `formsByGender`; tense order within a table is unchanged.