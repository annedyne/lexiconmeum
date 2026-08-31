## Data Override Implementation Plan

Implements the decision in [data-override-brainstorm.md](data-override-brainstorm.md): full-entry JSONL overrides matched on word plus part of speech.

Phases are sequential and independently reviewable. Each ends in a working state.

### Phase 1: Resolved lexical entry loading

Core mechanism. With no override file present, behavior is unchanged. The parser receives already-resolved `JsonNode` entries and does not know whether a node came directly from upstream data or from an override.

- `LoadProperties`: add an override file resource alongside the existing data file.
- `application.yml`: default the override file to a classpath location in main resources. The test profile overrides only the data file, so it inherits this default and applies overrides identically.
- Add a focused lexical entry key abstraction: extract lemma, part of speech and normalized etymology number from any Wiktionary-style entry. Both upstream data and override entries use the same extractor so matching keys stay aligned with Lexeme identity.
- Add an override index component with no file I/O: it is constructed from keyed override entries and returns the replacement for an upstream entry, if one exists. Missing or empty override input yields an empty index; an override whose key cannot be resolved warns and is ignored.
- Move JSONL reading and `ObjectMapper` use out of `WiktionaryLexicalDataParser` and into a lexical entry source/loader. This component owns consistent malformed-JSON logging, reads the main data file and override file, builds the override index, and emits resolved `JsonNode` entries.
- `WiktionaryLexicalDataParser`: parse one resolved `JsonNode` at a time. It remains responsible for deriving the POS parser key, delegating to the specialized parser, and staging/linking parsed lexemes.
- Overrides replace only. An override matching no upstream entry does nothing, so small fixtures are unaffected by the override set. Adding entries upstream lacks is deferred until needed.
- Test support obtains resolved nodes through the same entry-source/override path the application uses, while parser unit tests can pass hand-built `JsonNode`s directly.

Verify: existing test suite passes; new tests cover empty or absent override input as a no-op, malformed JSON failures in one JSONL loading path, invalid override keys being ignored, replacement of a matched entry, and an override matching nothing changing nothing.

### Phase 2: Match diagnostics

Makes override rot visible, which is the accepted mitigation for wholesale replacement.

- Override registry: track which overrides replaced an upstream entry and which matched nothing. Now the only signal that an override has gone stale, since a non-matching override is otherwise silent.
- Loader: log the override count plus the replaced and unmatched sets after loading completes.
- Warn when two override lines resolve to the same primary key, since the later one silently wins today.

Verify: tests assert the multi-match warning and that unmatched overrides are reported.

### Phase 3: Retiring the parser rescue

The corrected `sui` entry shipped with Phase 1, so only the rescue retirement remains. It is a separate decision because the correction copied from bundled data keeps the upstream `pronoun form` head template, which is exactly what the rescue exists to work around. Retiring the rescue means additionally editing the head template in the override, going beyond restoring the missing number tags.

- Override file: change the `sui` head template to a lemma pronoun template.
- `ParserConstants`: remove the reflexive pronoun lemma set and the non-lemma head template constant.
- `WiktionaryLexicalDataParser`: remove the flagged-word rescue from parser key derivation.
- Update the tests that assert the rescue path.

Verify: `sui` resolves and produces correct plural agreements with the rescue code gone; full suite passes.

Open question: whether an override should correct data upstream arguably has right (the entry genuinely is a non-lemma form) purely to simplify our parser, or whether the rescue should stay as deliberate parsing policy.

### Phase 4: Documentation

- `docs/ARCHITECTURE.md`: note the override file as an ingest input and where it applies in the pipeline.
- Short how-to for adding an override: copy the upstream line, edit, add provenance keys, add a test.
- `CLAUDE.md` and `AGENTS.md`: note the override file alongside the existing bundled data guidance.

### Deferred

- Automatic staleness detection. Not possible under wholesale replacement; revisit only if the override set grows beyond ad-hoc.