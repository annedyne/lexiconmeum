# Add Gerund to Ingest and expose in Details API

Re-use the existing methods handling the supine forms when possible. Rename method names specific to `supine` to `verbalNoun` when adapting them to handle both supine and gerund forms.

## Planned changes

- `src/main/java/com/annepolis/lexiconmeum/ingest/wiktionary/POSVerbParser.java`: recognize gerund-tagged verb forms, parse them through the shared verbal-noun path, and attach distinct supine and gerund declension sets to the verb.
- `src/main/java/com/annepolis/lexiconmeum/shared/model/grammar/partofspeech/ParticipleDeclensionSet.java`: generalize the supine-only builder and set-key selection so both non-gendered verbal-noun types are represented without a voice.
- `src/main/java/com/annepolis/lexiconmeum/shared/model/inflection/InflectionKey.java`: replace the supine-specific set-key helper with a verbal-noun key helper that keeps supine and gerund sets distinct.
- `src/main/java/com/annepolis/lexiconmeum/webapi/bff/lexemedetail/dtoassembly/inflection/ParticipleTableMapper.java`: map both supine and gerund sets through the verbal-noun declension-table layout so gerund forms appear in each gender table with their case forms.
- `src/test/java/com/annepolis/lexiconmeum/ingest/wiktionary/VerbParserTest.java`: cover gerund ingestion, cases, and coexistence with supine forms using the existing verb fixture.
- `src/test/java/com/annepolis/lexiconmeum/shared/model/grammar/partofspeech/ParticipleDeclensionSetTest.java`: cover construction and unique keys for both verbal-noun sets.
- `src/test/java/com/annepolis/lexiconmeum/webapi/bff/lexemedetail/dtoassembly/inflection/ParticipleTableMapperTest.java`: verify gerund labeling and declension output alongside supine output.
- `src/test/java/com/annepolis/lexiconmeum/webapi/bff/lexemedetail/LexemeDetailControllerIntegrationTest.java`: verify gerund forms are present in the serialized verb-details response.