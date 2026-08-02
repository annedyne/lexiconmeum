
package com.annepolis.lexiconmeum.testsupport;

import com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Central access to shared test support classes.
 *
 * JSON/Wiktionary helpers:
 * - com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager
 *
 * Fixture factories:
 * - com.annepolis.lexiconmeum.shared.model.LexemeFixtureFactory
 */
public final class TestSupport {

    private static final TestSupport INSTANCE = new TestSupport();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private TestSupport() {}

    public static TestSupport getInstance() {
        return INSTANCE;
    }

    public JsonTestDataManager getJsonTestDataManager() {
        return JsonTestDataManager.getInstance();
    }

    public static ObjectNode wiktionaryEntry(String lemma, String posTag) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("word", lemma);
        node.put("pos", posTag);
        return node;
    }

    public static ObjectNode wiktionaryEntry(String lemma, String posTag, String etymologyNumber) {
        ObjectNode node = wiktionaryEntry(lemma, posTag);
        node.put("etymology_number", etymologyNumber);
        return node;
    }

    public static String jsonLine(JsonNode node) {
        return node.toString();
    }

}
