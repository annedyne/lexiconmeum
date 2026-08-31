# Fix Reflexive Third-Person Pronoun forms

## Issue

- [ ] Reflexive third-person pronoun 'sui' (with forms 'sui', 'sibi', 'se' / 'sese') does not load.

**Root cause**: All the reflexive forms live in the single 'sui' entry, but that entry is
misclassified as a non-lemma form and skipped. Its head template is
`{name: "head", args.2: "pronoun form"}`, so `extractHeadTemplateNameFromRoot` yields the
string "pronoun form", which matches no `POSParserKey` (lemma pronouns such as ego/tu/ille
carry `args.2: "pronoun"`, which resolves to the PRONOUN key). With no key, the entry is
dropped before any parser sees it.

## Approach (settled)

Latin's reflexive pronoun is a single closed case with no nominative, so detection is an
explicit by-name flag, mirroring `SUPPLETIVE_PRONOUN_PARENTS`. Unlike that flag (checked in
POSAdjectiveParser), this one must be checked earlier — in `WiktionaryLexicalDataParser`
during parser-key derivation — because the entry is otherwise skipped before delegation.

When a flagged entry is forced to the PRONOUN key it should parse through the existing
pronoun path with no special handling; its forms already carry gender tags, so the
agreement mapper handles them.

Caveat: the 'sui' page has three etymologies — the reflexive pronoun ("pronoun form"), a
noun form of sūs ("noun form"), and a verb form of suō ("verb form"). The by-name flag must
also require the template string "pronoun form" so only the reflexive entry is rescued; the
noun and verb form entries stay correctly skipped.

### Phased plan

**Phase 1 — Rescue flagged reflexive-pronoun entries**
- Config (ParserConstants): add a set of reflexive-pronoun lemma names whose entry is
  wrongly tagged as a "pronoun form" (currently just 'sui').
- WiktionaryLexicalDataParser, parser-key derivation: when the derived template name does
  not resolve to a key, the entry's word is in the flagged set, and the template name is the
  non-lemma "pronoun form", force the PRONOUN key instead of skipping.
- Checkpoint: 'sui' loads as a pronoun lexeme with sui/sibi/se/sese forms in the detail
  response; the sui noun and verb form entries remain skipped; no regression elsewhere.

**Phase 2 — Tests**
- Derivation: flagged word + "pronoun form" template resolves to PRONOUN; same word with a
  "noun form" / "verb form" template still resolves to nothing (stays skipped); unflagged
  "pronoun form" entries stay skipped.
- Ingest/detail: 'sui' loads and its forms (sui, sibi, se, sese) appear in the response and
  are searchable.
- Checkpoint: full test suite green.

### Review

This implementation works, but only if I add plural tags to the reflexive form jsonl. I don't want to doctor the data directly, because that will have to be re-done every time the data is updated. We need a better solution.

