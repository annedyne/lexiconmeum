package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds hand-curated replacements for problematic Wiktionary entries, keyed by the same
 * lemma, part of speech and etymology number that identify a Lexeme. An override replaces
 * the whole upstream entry rather than patching it, so corrections survive without
 * depending on an upstream fix.
 *
 * Overrides only replace. An override matching no upstream entry does nothing.
 */
class WiktionaryLexicalDataOverrideIndex {

    private static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataOverrideIndex.class);

    private final Map<WiktionaryLexicalEntryKey, JsonNode> overridesByKey;
    private final WiktionaryLexicalEntryKeyExtractor keyExtractor;

    private WiktionaryLexicalDataOverrideIndex(
            Map<WiktionaryLexicalEntryKey, JsonNode> overridesByKey,
            WiktionaryLexicalEntryKeyExtractor keyExtractor
    ) {
        this.overridesByKey = Map.copyOf(overridesByKey);
        this.keyExtractor = keyExtractor;
    }

    static WiktionaryLexicalDataOverrideIndex empty(WiktionaryLexicalEntryKeyExtractor keyExtractor) {
        return new WiktionaryLexicalDataOverrideIndex(Map.of(), keyExtractor);
    }

    static WiktionaryLexicalDataOverrideIndex fromOverrides(
            Iterable<JsonNode> overrides,
            WiktionaryLexicalEntryKeyExtractor keyExtractor
    ) {
        Map<WiktionaryLexicalEntryKey, JsonNode> overridesByKey = new LinkedHashMap<>();
        for (JsonNode override : overrides) {
            Optional<WiktionaryLexicalEntryKey> key = keyExtractor.extract(override);
            if (key.isEmpty()) {
                logger.warn("Ignoring override with unresolvable lemma or part of speech: {}", override);
                continue;
            }
            overridesByKey.put(key.get(), override);
        }
        return new WiktionaryLexicalDataOverrideIndex(overridesByKey, keyExtractor);
    }

    int size() {
        return overridesByKey.size();
    }

    /**
     * Returns the override replacing this entry, or the entry itself when none does.
     */
    JsonNode resolve(JsonNode entry) {
        if (overridesByKey.isEmpty()) {
            return entry;
        }
        Optional<WiktionaryLexicalEntryKey> key = keyExtractor.extract(entry);
        if (key.isEmpty()) {
            return entry;
        }
        JsonNode override = overridesByKey.get(key.get());
        if (override == null) {
            return entry;
        }
        logger.debug("Replacing upstream entry {} with override", key.get());
        return override;
    }
}
