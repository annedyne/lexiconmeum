# Add Supine Tense

## Goal

Ingest Wiktionary verb forms tagged `supine`, show them in `LEXEME_DETAIL` after existing participle tenses, and make them available to PREFIX and SUFFIX search.

## Current State

- Supines occur on verb `forms` entries, normally with `supine`, `noun-from-verb`, and an accusative or ablative tag.
- `POSVerbParser` sends these entries through the normal conjugation path, but `supine` has no tag mapping. `Conjugation.Builder` therefore has no required tense and rejects the form.
- `SearchableVerbFormsExtractor` already indexes all accepted verb inflections, so no new search-index path should be needed once supines are retained.
- `POSParticipleParser` already maps case tags into `Participle` objects and packages them in `ParticipleDeclensionSet`s, which `VerbDetails` stores.
- Supines are non-gendered and originate in the parent verb's `forms` array, so they should use that same model path directly, without `StagedParticipleData` or a second JSON entry.
- `ParticipleTableMapper` currently assumes gendered declension sets. It needs an explicit supine response path rather than treating the two supine cases as a gendered participle table.

## Phase 1: Model And Tag Mapping

1. Add `SUPINE` to `GrammaticalTense` and `GrammaticalParticipleTense`, ordered after existing values and named `Supine` in the API.
2. Extend the existing `ParticipleDeclensionSet` construction path to represent a supine explicitly, without inventing a grammatical voice. The set key must remain stable and distinct from ordinary participles.
3. Reuse the existing `Participle.Builder` case-tag mapping for supine forms. Extract only the small common form-building helper from `POSParticipleParser` if needed; do not duplicate or generalize the staging process.

Acceptance: an accusative/ablative supine can be represented in one `ParticipleDeclensionSet` with its case and form intact, and the set is distinguishable from normal participles.

## Phase 2: Ingestion Coverage

1. In `POSVerbParser`, split valid verb forms by tag: retain ordinary forms on the current conjugation path; collect `supine` forms as `Participle` objects using the shared form-building logic.
2. Build the supine `ParticipleDeclensionSet` during parent-verb parsing and add it directly to the verb's `VerbDetails.Builder`. Do not create or stage `StagedParticipleData`.
3. Preserve any pre-existing `VerbDetails` when attaching the set, following the rebuild pattern in `StagedParticipleData.link()`.
4. Add focused `POSVerbParser` coverage using existing fixture data (`sequor` contains accusative and ablative supines, including alternatives), plus parser integration coverage for direct attachment.

Acceptance: `secūtum`/`sequūtum` and `secūtū`/`sequūtū` are retained under `VerbDetails` for `sequor`; no staging/linking or compound-tense behavior changes.

## Phase 3: Lexeme-Detail Mapping

1. Extend the participle response DTO only as needed to represent a non-gendered supine entry with case-to-form values; do not fabricate gender, number, or a full participle declension grid.
2. Extend `ParticipleTableMapper` to recognize the direct supine set in `VerbDetails`, map its accusative and ablative forms, and append `Supine` after the normal participle tenses.
3. Keep `ConjugationTableMapper` unchanged: supines never enter the conjugation collection.
4. Add mapper tests for name, order, accusative and ablative forms, and alternate-form behavior; add controller/integration coverage for the JSON shape.

Acceptance: `LEXEME_DETAIL` exposes one `Supine` entry after existing participle tenses, with its actual case forms and no invented gender/number data.

## Phase 4: Search And Regression Verification

1. Add focused autocomplete/index tests proving a supine is found by both PREFIX and SUFFIX routes.
2. Run focused parser, mapper, and text-search tests; then run `./mvnw test`.
3. Verify the bundled lexical fixture needs no mass change; use existing `sequor` data unless a minimal dedicated fixture is required.

Acceptance: supines are searchable by prefix and suffix, and all tests pass.

## Scope

- No parser rewrite, new index type, staging change, or bundled-data reformatting.
- No change to normal participle linking or compound-tense generation.
