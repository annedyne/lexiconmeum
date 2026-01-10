package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.HEAD_TEMPLATES;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.NAME;

public class POSAdjectiveParserTest {

    private POSAdjectiveParser parser;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        parser = new POSAdjectiveParser();
        mapper = new ObjectMapper();
    }

    @ParameterizedTest
    @ValueSource(strings = {"la-adj", "la-adj-comp", "la-adj-sup"})
    void validateReturnsTrueForSupportedAdjectiveTemplates(String templateName) {
        JsonNode root = createTestJsonNode(templateName);
        Assertions.assertTrue(parser.validate(root), "Should support template: " + templateName);
    }

    @Test
    void validateReturnsFalseForUnsupportedTemplate() {
        JsonNode root = createTestJsonNode("la-noun");
        Assertions.assertFalse(parser.validate(root));
    }

    @Test
    void validateReturnsFalseWhenHeadTemplatesAreMissing() {
        ObjectNode root = mapper.createObjectNode();
        Assertions.assertFalse(parser.validate(root));
    }

    @Test
    void validateReturnsFalseWhenHeadTemplatesAreEmpty() {
        ObjectNode root = mapper.createObjectNode();
        root.putArray(HEAD_TEMPLATES.get());
        Assertions.assertFalse(parser.validate(root));
    }

    private JsonNode createTestJsonNode(String templateName) {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode headTemplates = root.putArray(HEAD_TEMPLATES.get());
        ObjectNode template = headTemplates.addObject();
        template.put(NAME.get(), templateName);
        return root;
    }

    @Test
    void validatesComparativeAdjective() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("levior", "testDataAdjective.jsonl");
        Assertions.assertTrue(parser.validate(root));
    }

    @Test
    void validatesSuperlativeAdjective() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("levissimus", "testDataAdjective.jsonl");
        Assertions.assertTrue(parser.validate(root));
    }

    @Test
    void doesNotValidateNonAdjective() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("nox", "testDataNoun.jsonl");
        Assertions.assertFalse(parser.validate(root));
    }
}
