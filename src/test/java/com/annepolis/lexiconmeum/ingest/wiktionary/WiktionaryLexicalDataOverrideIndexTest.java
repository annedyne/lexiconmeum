package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

import static com.annepolis.lexiconmeum.testsupport.TestSupport.wiktionaryEntry;
import static org.junit.jupiter.api.Assertions.*;

class WiktionaryLexicalDataOverrideIndexTest {

    private static final ParserSupport PARSER_SUPPORT =
            new ParserSupport(new LexicalTagResolver(), ParseMode.STRICT);

    private static final WiktionaryLexicalEntryKeyExtractor KEY_EXTRACTOR = new WiktionaryLexicalEntryKeyExtractor(PARSER_SUPPORT);

    // Marks a node as coming from the override file rather than upstream.
    private static final String MARKER = "_override_reason";

    @Test
    void resolveReturnsOverrideGivenMatchingPrimaryKey() {
        WiktionaryLexicalDataOverrideIndex registry = registryOf(
                override("sui", "pron", "1"));

        JsonNode resolved = registry.resolve(entry("sui", "pron", "1"));

        assertTrue(resolved.has(MARKER));
    }

    @Test
    void resolveReturnsOriginalEntryGivenNoMatchingOverride() {
        WiktionaryLexicalDataOverrideIndex registry = registryOf(
                override("sui", "pron", "1"));

        assertFalse(registry.resolve(entry("amo", "verb", "1")).has(MARKER),
                "different lemma should not match");
        assertFalse(registry.resolve(entry("sui", "noun", "1")).has(MARKER),
                "different part of speech should not match");
    }

    @Test
    void resolveDistinguishesEntriesByEtymologyNumber() {
        WiktionaryLexicalDataOverrideIndex registry = registryOf(
                override("sui", "pron", "2"));

        assertTrue(registry.resolve(entry("sui", "pron", "2")).has(MARKER));
        assertFalse(registry.resolve(entry("sui", "pron", "1")).has(MARKER));
    }

    @Test
    void resolveTreatsMissingEtymologyNumberAsOne() {
        WiktionaryLexicalDataOverrideIndex registry = registryOf(
                override("sui", "pron"));

        assertTrue(registry.resolve(entry("sui", "pron", "1")).has(MARKER),
                "an override with no etymology number should match an entry numbered 1");
    }

    @Test
    void resolveReturnsOriginalEntryGivenEmptyOverrides() {
        JsonNode entry = entry("sui", "pron", "1");

        assertSame(entry, WiktionaryLexicalDataOverrideIndex.empty(KEY_EXTRACTOR).resolve(entry));
    }

    @Test
    void loadIgnoresOverrideWithUnknownPartOfSpeech() {
        WiktionaryLexicalDataOverrideIndex registry = registryOf(
                override("sui", "notAPartOfSpeech", "1"),
                override("amo", "verb", "1"));

        assertFalse(registry.resolve(entry("sui", "notAPartOfSpeech", "1")).has(MARKER),
                "an unkeyable override should be dropped, not applied");
        assertTrue(registry.resolve(entry("amo", "verb", "1")).has(MARKER),
                "a dropped override should not stop later lines from loading");
    }

    @Test
    void lastOverrideWinsGivenDuplicatePrimaryKeys() {
        WiktionaryLexicalDataOverrideIndex registry = registryOf(
                override("sui", "pron", "1", "first"),
                override("sui", "pron", "1", "second"));

        assertEquals("second", registry.resolve(entry("sui", "pron", "1")).path(MARKER).asString());
    }

    private static WiktionaryLexicalDataOverrideIndex registryOf(JsonNode... overrides) {
        return WiktionaryLexicalDataOverrideIndex.fromOverrides(List.of(overrides), KEY_EXTRACTOR);
    }

    private static JsonNode override(String lemma, String posTag) {
        return override(wiktionaryEntry(lemma, posTag), "corrected");
    }

    private static JsonNode override(String lemma, String posTag, String etymologyNumber) {
        return override(lemma, posTag, etymologyNumber, "corrected");
    }

    private static JsonNode override(String lemma, String posTag, String etymologyNumber, String markerValue) {
        return override(wiktionaryEntry(lemma, posTag, etymologyNumber), markerValue);
    }

    private static JsonNode override(ObjectNode node, String markerValue) {
        node.put(MARKER, markerValue);
        return node;
    }

    private static JsonNode entry(String lemma, String posTag, String etymologyNumber) {
        return wiktionaryEntry(lemma, posTag, etymologyNumber);
    }
}
