package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Component
class WiktionaryLexicalDataEntrySource {

    private static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataEntrySource.class);

    private final Resource lexicalData;
    private final Resource overrideData;
    private final JsonlResourceReader jsonlResourceReader;
    private final WiktionaryLexicalEntryKeyExtractor keyExtractor;

    WiktionaryLexicalDataEntrySource(
            LoadProperties loadProperties,
            JsonlResourceReader jsonlResourceReader,
            WiktionaryLexicalEntryKeyExtractor keyExtractor
    ) {
        this.lexicalData = loadProperties.getDataFile();
        this.overrideData = loadProperties.getOverrideFile();
        this.jsonlResourceReader = jsonlResourceReader;
        this.keyExtractor = keyExtractor;
    }

    void readResolvedEntries(Consumer<JsonNode> entryConsumer) throws IOException {
        WiktionaryLexicalDataOverrideIndex overrideIndex = loadOverrides();
        jsonlResourceReader.read(lexicalData, entry -> entryConsumer.accept(overrideIndex.resolve(entry)));
    }

    private WiktionaryLexicalDataOverrideIndex loadOverrides() throws IOException {
        List<JsonNode> overrides = jsonlResourceReader.readIfPresent(overrideData);
        WiktionaryLexicalDataOverrideIndex overrideIndex = WiktionaryLexicalDataOverrideIndex.fromOverrides(overrides, keyExtractor);
        logger.info("Loaded {} lexical data override(s)", overrideIndex.size());
        return overrideIndex;
    }
}
