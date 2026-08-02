package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

@Component
class WiktionaryLexicalEntryKeyExtractor {

    private static final Logger logger = LogManager.getLogger(WiktionaryLexicalEntryKeyExtractor.class);

    private final ParserSupport parserSupport;

    WiktionaryLexicalEntryKeyExtractor(ParserSupport parserSupport) {
        this.parserSupport = parserSupport;
    }

    Optional<WiktionaryLexicalEntryKey> extract(JsonNode entry) {
        return parserSupport.extractPrimaryKeyData(entry, logger)
                .map(primaryKeyData -> new WiktionaryLexicalEntryKey(
                        primaryKeyData.lemma(),
                        primaryKeyData.partOfSpeech(),
                        primaryKeyData.etymologyNumber()
                ));
    }
}
