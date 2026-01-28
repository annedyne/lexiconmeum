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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.FORMS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerbParserTest {

    private static final EsseFormProvider ESSE_FORM_PROVIDER = new EsseFormProvider();
    private static final LexicalTagResolver LEXICAL_TAG_RESOLVER = new LexicalTagResolver();
    private static final ParserSupport PARSER_SUPPORT = new ParserSupport(LEXICAL_TAG_RESOLVER, ParseMode.STRICT);
    private static LexemeBuilder sequorLexemeBuilder;

    @ParameterizedTest
    @MethodSource("expectedCompoundSequorTenseForms")
    void generatesAllCompoundForms(ConjugationTestCase testCase ) throws IOException {

        if (sequorLexemeBuilder == null) {
            JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("sequor", "testDataVerb.jsonl");

            POSVerbParser parser = new POSVerbParser(new EsseFormProvider(), PARSER_SUPPORT);
            sequorLexemeBuilder = new LexemeBuilder("testLemma", PartOfSpeech.VERB, "1");
            parser.addInflections(sequorLexemeBuilder, root.path(FORMS.get()) );
        }

        Inflection inflection = sequorLexemeBuilder.getInflections().get(testCase.key);

        Assertions.assertNotNull(inflection, "No inflection found for key: " + testCase.key);
        Assertions.assertInstanceOf(Conjugation.class, inflection, "Expected Conjugation but got: " + inflection.getClass());

        Conjugation conjugation = (Conjugation) inflection;
        assertEquals(testCase.expectedForm, conjugation.getForm(),
                "Form mismatch for " + testCase.mood + " " + testCase.tense + " " + testCase.number + " " + testCase.person);
    }

    public static Stream<ConjugationTestCase> expectedCompoundSequorTenseForms() {
        List<ConjugationTestCase> testCases = new ArrayList<>();

        for (GrammaticalMood mood  : GrammaticalMood.values()) {
            for (GrammaticalTense tense : GrammaticalTense.values()) {
                for (GrammaticalNumber number : GrammaticalNumber.values()) {
                    for (GrammaticalPerson person : GrammaticalPerson.values()) {
                        String esseForm = ESSE_FORM_PROVIDER.getForm(mood, tense, number, person);
                        if(esseForm != null) {
                            String expectedForm = generateForm("secūtus", esseForm);
                            String key = InflectionKey.joinConjugationParts(
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

    static String generateForm ( String participle,  String esseForm ) {
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
