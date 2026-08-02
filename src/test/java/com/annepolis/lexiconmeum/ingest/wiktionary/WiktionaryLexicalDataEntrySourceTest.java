package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.annepolis.lexiconmeum.testsupport.TestSupport.jsonLine;
import static com.annepolis.lexiconmeum.testsupport.TestSupport.wiktionaryEntry;
import static org.junit.jupiter.api.Assertions.*;

class WiktionaryLexicalDataEntrySourceTest {

    private static final String MARKER = "_override_reason";

    private final WiktionaryLexicalEntryKeyExtractor keyExtractor =
            new WiktionaryLexicalEntryKeyExtractor(new ParserSupport(new LexicalTagResolver(), ParseMode.STRICT));

    @Test
    void readResolvedEntriesEmitsUpstreamEntriesGivenNoOverrideFile() throws IOException {
        WiktionaryLexicalDataEntrySource source = entrySource(
                jsonlResource(jsonLine(entry("sui", "pron", "1"))),
                null
        );

        List<JsonNode> nodes = collect(source);

        assertEquals(1, nodes.size());
        assertFalse(nodes.get(0).has(MARKER));
    }

    @Test
    void readResolvedEntryReplacesMatchingUpstreamEntries() throws IOException {
        WiktionaryLexicalDataEntrySource source = entrySource(
                jsonlResource(
                        jsonLine(entry("sui", "pron", "1")),
                        jsonLine(entry("amo", "verb", "1"))
                ),
                jsonlResource(jsonLine(override("sui", "pron", "1")))
        );

        List<JsonNode> nodes = collect(source);

        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0).has(MARKER));
        assertFalse(nodes.get(1).has(MARKER));
    }

    @Test
    void readResolvedEntryIgnoresOverrideMatchingNoUpstreamEntries() throws IOException {
        WiktionaryLexicalDataEntrySource source = entrySource(
                jsonlResource(jsonLine(entry("amo", "verb", "1"))),
                jsonlResource(jsonLine(override("sui", "pron", "1")))
        );

        List<JsonNode> nodes = collect(source);

        assertEquals(1, nodes.size());
        assertEquals("amo", nodes.get(0).path("word").asString());
        assertFalse(nodes.get(0).has(MARKER));
    }

    private WiktionaryLexicalDataEntrySource entrySource(Resource dataFile, Resource overrideFile) {
        LoadProperties loadProperties = new LoadProperties();
        loadProperties.setDataFile(dataFile);
        loadProperties.setOverrideFile(overrideFile);
        return new WiktionaryLexicalDataEntrySource(loadProperties, new JsonlResourceReader(), keyExtractor);
    }

    private static List<JsonNode> collect(WiktionaryLexicalDataEntrySource source) throws IOException {
        List<JsonNode> nodes = new ArrayList<>();
        source.readResolvedEntries(nodes::add);
        return nodes;
    }

    private static Resource jsonlResource(String... lines) {
        byte[] content = String.join("\n", lines).getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(content);
    }

    private static JsonNode override(String lemma, String posTag, String etymologyNumber) {
        ObjectNode node = entry(lemma, posTag, etymologyNumber);
        node.put(MARKER, "corrected");
        return node;
    }

    private static ObjectNode entry(String lemma, String posTag, String etymologyNumber) {
        return wiktionaryEntry(lemma, posTag, etymologyNumber);
    }
}
