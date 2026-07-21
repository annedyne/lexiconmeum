# Handle Pronouns with non-gendered agreements

## Issues

1. - [X] Declensions of personal pronoun 'ego' not loading in front-end.

**Root cause**: The declensions of pronoun 'ego' have no gender, but the agreementTableMapper assumes all agreements have some gender. This prevents any of ego's forms from being added to the response dto.

**Fix**: Logic now defaults to assigning all genders to agreements that have no gender-specific forms. 


2. - [ ] 'Nos', the plural form of 'ego', loads as a separate word instead of populating the plural agreement section of 'ego'.

3. - [ ] 'vos', the plural form of 'tu', loads as a separate word instead of populating the plural agreement section of 'tu'.

**Root cause (both)**: nos/vos are separate Wiktionary lemma entries. Most other pronouns package all their forms together, so there is no existing mechanism to stage a pronoun for later linking. nos has a form_of tag pointing to ego; vos has none (only a "…plural of tū" gloss and tū buried in its `related` list).

## Approach for issues 2 & 3 (settled)

Latin's suppletive personal-pronoun plurals are a closed set of exactly two pairs (nos→ego, vos→tu), so detection is a small explicit child→parent map rather than tag/gloss inference. Merging reuses the existing linkable-staging pipeline (precedent: participles, adjective degrees). Child plural forms already carry a `plural` number tag and the parent's forms carry `singular`, so agreements merge without collision or transformation. Absorbing the child is safe for search since all inflected forms (not just lemmas) are indexed, so nos/vos remain findable as forms of ego/tu.

Requires ego present in bundled data (added).

### Phased plan

**Phase 1 — Child detection + staging (child no longer standalone)**
- Config (ParserConstants or a small pronoun config): add the child→parent personal-pronoun lemma map.
- POSAdjectiveParser, PRONOUN case: if the lemma is a known child, stage it as a linkable instead of processing immediately; otherwise unchanged.
- New StagedPronounData (implements LinkableData): stub link() (no merge yet); parent-link part of speech is PRONOUN.
- Checkpoint: nos/vos no longer emitted as standalone lexemes; parents ego/tu still load; no regression elsewhere.

**Phase 2 — Merge child forms into parent**
- StagedPronounData.link(): add the child's plural agreements onto the parent lexeme.
- Build the child lexeme via the existing pronoun path to obtain those agreements.
- Checkpoint: ego shows nos plural forms and tu shows vos plural forms in the detail response; parent singular forms intact.

**Phase 3 — Verify search + tests**
- Confirm merged plural forms (nos/vos and their cases) still resolve via search to the parent lexeme.
- Tests: linking merges nos into ego and vos into tu; child not emitted standalone; merged plural forms searchable; unresolved-parent case degrades gracefully.
- Checkpoint: full test suite green.





