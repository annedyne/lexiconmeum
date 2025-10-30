package com.annepolis.lexiconmeum.ingest.wiktionary;


import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.FORMS;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.WORD;

public class VerbParserTest {

    private static final EsseFormProvider ESSE_FORM_PROVIDER = new EsseFormProvider();
    private static LexemeBuilder sequorLexemeBuilder;

    private List<JsonNode> getJsonRoot() throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("testDataVerb.jsonl");

        if (inputStream == null) {
            throw new IOException("Resource not found: testDataVerb.jsonl");
        }

        List<JsonNode> nodes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
               nodes.add(new ObjectMapper().readTree(line));
            }
        }
        return nodes;
    }

    @Test
    void testValidateConfirmsStandardVerbNodeIsValid() throws IOException {
        JsonNode root = getJsonRoot().get(0);
        VerbParser parser = new VerbParser(new LexicalTagResolver(),  new EsseFormProvider());
        Assertions.assertTrue(parser.validate(root));
    }


    @ParameterizedTest
    @MethodSource("expectedCompoundSequorTenseForms")
    void testGeneratesAllCompoundForms(ConjugationTestCase testCase ) throws IOException {

        if (sequorLexemeBuilder == null) {
            JsonNode root = getJsonRoot().stream()
                    .filter(node -> node.path(WORD.get()).asText().equals("sequor"))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Lexeme 'sequor' not found"));

            VerbParser parser = new VerbParser(new LexicalTagResolver(), new EsseFormProvider());
            sequorLexemeBuilder = new LexemeBuilder("testLemma", PartOfSpeech.VERB, "1");
            parser.addInflections(root.path(FORMS.get()), sequorLexemeBuilder);
        }

        Inflection inflection = sequorLexemeBuilder.getInflections().get(testCase.key);

        Assertions.assertNotNull(inflection, "No inflection found for key: " + testCase.key);
        Assertions.assertInstanceOf(Conjugation.class, inflection, "Expected Conjugation but got: " + inflection.getClass());

        Conjugation conjugation = (Conjugation) inflection;
        Assertions.assertEquals(testCase.expectedForm, conjugation.getForm(),
                "Form mismatch for " + testCase.mood + " " + testCase.tense + " " + testCase.number + " " + testCase.person);

    }

    static public Stream<ConjugationTestCase> expectedCompoundSequorTenseForms() {
        List<ConjugationTestCase> testCases = new ArrayList<>();

        for (GrammaticalMood mood  : GrammaticalMood.values()) {
            for (GrammaticalTense tense : GrammaticalTense.values()) {
                for (GrammaticalNumber number : GrammaticalNumber.values()) {
                    for (GrammaticalPerson person : GrammaticalPerson.values()) {
                        String esseForm = ESSE_FORM_PROVIDER.getForm(mood, tense, number, person);
                        if(esseForm != null) {
                            String expectedForm = generateForm("secūtus", esseForm);
                            String key = InflectionKey.joinParts(
                                    GrammaticalVoice.ACTIVE,
                                    mood,
                                    tense,
                                    person,
                                    number
                            );

                            testCases.add(new ConjugationTestCase(key, expectedForm, mood, tense, number, person));
                        }
                    }
                }
            }
        }

        return testCases.stream();
    }

    static String generateForm ( String participle, 
                                 String esseForm
    ) {
        return participle + " " + esseForm;
    }

    static class ConjugationTestCase {
        final String key;
        final String expectedForm;
        final GrammaticalMood mood;
        final GrammaticalTense tense;
        final GrammaticalNumber number;
        final GrammaticalPerson person;

        ConjugationTestCase(String key, String expectedForm, GrammaticalMood mood,
                            GrammaticalTense tense, GrammaticalNumber number, GrammaticalPerson person) {
            this.key = key;
            this.expectedForm = expectedForm;
            this.mood = mood;
            this.tense = tense;
            this.number = number;
            this.person = person;
        }

        @Override
        public String toString() {
            return mood + "|" + tense + "|" + number + "|" + person + " -> " + expectedForm;
        }
    }
}
