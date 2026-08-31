## Data Override Brainstorm

I want to be able to override problematic Wiktionary data, either by overriding the JSON itself or by overriding models from a secondary data store.

An immediate use-case example is an adhoc problem like the 'sui' JSON missing 'number' tags for its plural forms. This may be intentional, or it may be fixed eventually, but either way it causes problems, and I want to be able to override wiktionary's data in a way that won't have to be redone after wiktionary data updates or keep LexiconMeum dependent on Wiktionary fixes.


I'd like a range of options from low effort (like an internal partial jsonl file) to high along with tradeoffs. What would be standard industry approaches? Feel free to ask questions if you need more info.

## Where An Override Can Be Applied

Four seams exist today, in pipeline order. The choice of seam matters more than the file format.

- Offline, before the app sees data: patch the JSONL as a data-prep step, app stays unaware.
- Raw JSON, at read time: patch each entry between line-read and parser delegation, so all downstream parsing, tag mapping, and linking still apply.
- Parse time: special cases inside the parsers. This is the current de facto mechanism (`ParserConstants` plus the reflexive-pronoun rescue).
- Domain model, after linking and before distribution: `LexemeDistributor` is a single chokepoint behind an interface, so finalized lexemes can be corrected without touching parsers.

## Options, Low To High Effort

### A. Status quo: parser special cases
- Hardcoded sets/maps in the ingest package, consulted by parsers.
- Cheapest per fix, zero new infrastructure.
- Overrides scatter across parsing code, are invisible as a set, and mix data correction with parsing logic. Does not scale past a handful.

### B. Second JSONL of full replacement entries
- Add an optional override data file alongside the main one; entries replace upstream ones matched by primary key.
- Very low effort, reuses the whole existing parse path, easy to reason about.
- Whole entry must be hand-copied, so it permanently forks that entry and stops inheriting upstream improvements. Fine for a small closed set, poor as a general mechanism.

### C. Entry-level patch file over raw JSON
- Declarative per-entry patches (JSON Merge Patch or JSON Patch are the standard formats) applied to matching raw entries before parser delegation.
- Surgical: untouched fields keep tracking upstream. Standard, well-understood format.
- Notably this seam also subsumes the existing reflexive-pronoun rescue, since the real defect there is a wrong head-template value in the source JSON, not a parsing gap.
- Needs an entry selector and a way to target a specific form inside an array without relying on positional index, which shifts on refresh. Requires a "patch matched nothing" warning or patches rot silently.

### D. Domain-model override layer
- Declarative corrections expressed in the project's own vocabulary (lemma plus part of speech, inflection key, grammatical features), applied post-linking and pre-distribution.
- Most precise and most reviewable, and it can express things the raw JSON cannot: adding a missing inflection, correcting an inflection class, fixing a linked form.
- More machinery: needs an addressing scheme for inflections and its own applier. Bypasses parsing, so a model override and a JSON override of the same entry can disagree.

### E. Secondary datastore of curated corrections
- Same applier as D, but corrections live in a database or embedded store loaded at startup.
- Buys editing without redeploy, provenance and audit trail, and a possible stewardship UI later.
- Real operational weight (schema, migrations, backup, an editing path). Overkill until the override count grows or non-developers need to edit.

### F. Forked data build artifact
- An offline build step produces a patched, versioned `lexicalData.jsonl` with a checksum; the app consumes it and never knows overrides exist.
- Cleanest separation, and the standard industry shape for third-party reference data. Corrections are validated at build time, not in production.
- Needs a data pipeline and release process of its own, plus somewhere to keep the patch set. Highest effort, best endgame if data refresh becomes routine.

## Standard Industry Framing

- The general pattern is a curated correction layer applied last with explicit precedence: upstream source stays pristine, corrections are a separate reviewable artifact, and the merge rule is stated (override always wins vs. override only fills gaps).
- Master data management calls these steward exceptions; analytics pipelines call it a manual-override seed joined at the final layer. Same idea either way.
- Patching upstream data with declarative patch files is the same shape as a distro patch queue, which is why the JSON patch formats in option C are the conventional choice.

## Practices To Adopt Regardless Of Option

- Every override records a reason, an upstream reference, and a date.
- Detect staleness: flag when an override has become a no-op because upstream fixed it, so it can be retired. This is the main thing that stops an override set from becoming permanent cruft.
- One fixture test per override, so upstream refresh surfaces breakage as a test failure.
- Keep all overrides in one location and log a count at startup.

## Recommendation

- Start with C. The known cases, including `sui` and the reflexive-pronoun rescue, are all defects in the raw JSON, and fixing them there keeps every downstream parsing and linking behavior intact and lets the parser special cases be retired.
- Escalate to D only for cases that genuinely cannot be expressed as source JSON, keeping both layers rather than migrating.
- Consider F if and when refreshing upstream data becomes a regular operation rather than an occasional one.

## Open Questions

- How often is the upstream data actually refreshed, and is it a manual step today?
- Are the expected overrides mostly missing or wrong tags, or do some need whole forms invented that upstream lacks entirely?
- Should overrides ever apply to the bundled partial data used by tests, or only to the full production data file?
- Is editing overrides without a redeploy ever a requirement, or is a code-review-and-deploy cycle acceptable?

### Answers to Open Questions:

- the upstream data is rarely refreshed because it's a pain to download. So it's still a manual step.
- I think expected overrides are mostly within the structure of individual JSON lines -- stuff missing, wrong, inconvenient, or misaligned with my data model.
- The overrides should be applied the same way in tests as they are in production runtime.
- I think code-review-and-deploy cycle is fine for now. I want to be able to make overrides, test the code, and deploy seamlessly without having to separately deploy the overrides.

I think option B may be adequate for now. I'm not too worried about missing out on fixes in other parts of a JSON line since I rarely refresh the data file anyway. Also, I don't anticipate needing too many overrides - it's more of an adhoc thing. What are your thoughts?

## Decision: Option B, full-entry replacement overrides

An override file of complete JSONL entries, bundled on the classpath, replacing upstream entries matched on the Lexeme primary key.

### Why B over the alternatives

- C rejected: surgical patches earn their keep across frequent refreshes, and refreshes here are rare and manual. Also awkward for entries upstream lacks entirely, which B handles for free.
- D and E rejected: the expected corrections are structural fixes within a single JSON line, so the raw-JSON seam is the right one. E additionally buys editing without redeploy, which is explicitly not wanted.
- A retired as it is superseded: the reflexive-pronoun rescue exists because the `sui` pronoun entry carries a non-lemma head template, which an override entry corrects directly.

### Shape

- Override file lives in main resources and loads as a classpath resource, so the test profile and production runtime apply overrides identically and overrides deploy with the application.
- Matching is on the primary key that already identifies a Lexeme (lemma, part of speech, etymology number), reusing the parser's existing primary-key extraction so an override keys exactly as the parsed entry does. Including the etymology number is what keeps entries split by etymology distinct.
- Overrides only replace: an upstream entry that matches is parsed as the override instead, and an override matching nothing does nothing. Adding entries upstream lacks is deliberately out of scope until something needs it.
- Each override line carries underscore-prefixed provenance keys (reason, upstream reference, date). Parsing reads named paths and ignores unknown keys, so these need no parser change and are the only readable record of what was changed and why.
- Startup logs the override count, which overrides matched an upstream entry, and which did not.

### Consequences accepted

- An overridden entry permanently forks from upstream. Acceptable given rare refreshes and a small ad-hoc override set.
- Full entries are large single lines, so the git diff does not show the delta against upstream. The provenance keys are the mitigation.
- Staleness cannot be detected automatically, since replacement is wholesale and there is nothing to compare. The match log plus one test per override is the proportionate substitute at this scale.
- Revisit if the override set grows well beyond ad-hoc, or if upstream refresh becomes routine.
