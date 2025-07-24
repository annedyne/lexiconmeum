package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import com.annepolis.lexiconmeum.lexeme.detail.verb.InflectionKey;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = WiktionaryLexicalDataParser.class)
class WiktionaryLexicalDataParserTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WiktionaryLexicalDataParser parser;

    private List<Lexeme> verbLexemes;
    private List<Lexeme> nounLexemes;

    public List<Lexeme> getVerbLexemes() throws IOException {
        if(verbLexemes == null) {
            parseVerbLexemes();
        }
        return verbLexemes;
    }

    public List<Lexeme> getNounLexemes() throws IOException {
        if(nounLexemes == null) {
            parseNounLexemes();
        }
        return nounLexemes;
    }

    @Test
    void resourceExists() {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");

        assertTrue(resource.exists(), "Expected testDataRaw.jsonl to be present on the classpath");
    }

    @Test
    void nounResourceExists() {
        Resource resource = resourceLoader.getResource("classpath:testDataNoun.jsonl");

        assertTrue(resource.exists(), "Expected testDataNoun.jsonl to be present on the classpath");
    }

    /**
     * Using the parser to test the json file
     */
    @Test
    void JsonlFileParsesWithoutError() {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        assertDoesNotThrow(() -> {
            try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                BufferedReader br = new BufferedReader(reader);
                parser.readJsonLine(br);
            }
        });
    }

    @Test
    void testLoadJsonFile() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);

            assertEquals(3, lexemes.size());
            assertEquals(GrammaticalPosition.VERB, lexemes.get(0).getPosition());
            assertEquals(GrammaticalPosition.NOUN, lexemes.get(1).getPosition());
        }
    }

    @Test
    void testLoadWord() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);
            assertEquals("amo", lexemes.get(0).getLemma());

        }
    }

    @Test
    void declensionsInflectionsLoaded() throws Exception {
        Optional<Inflection> genitive = getNounLexemes().stream()
           .filter(g -> g.getLemma().equals("poculum"))
           .findFirst()
              .flatMap(l -> l.getInflections().stream()
                .filter(i -> i.getForm().equals("pōculī"))
                .findFirst());

        assertTrue(genitive.isPresent());
    }

    @SuppressWarnings("java:S2699") // Yes this does have an assertion
    @Test
    void glossesAreParsedAndPopulated() throws Exception {
        getVerbLexemes().get(0).getSenses().stream()
                .filter(s -> s.getGloss().contains("to love"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing gloss 'to love'"));
    }

    @Test
    void parserMapsFutureAndPerfectTagsToFuturePerfectTense() throws Exception {
        Inflection tenseTag = getVerbLexemes().get(0).getInflections().stream()
                .filter(g -> g.getForm().equals("amāverō"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing future-perfect test form"));

        assertEquals(GrammaticalTense.FUTURE_PERFECT, ((Conjugation) tenseTag).getTense());
    }

    @Test
    void InflectionsWithDuplicateTagsAreSetAsAlternativeForms() throws IOException {
        Optional<Inflection> maybeConjugation = getVerbLexemes().get(0).getInflections().stream()
                .filter(g -> "amārō".equals(g.getAlternativeForm()))
                .findFirst();
        assertTrue(maybeConjugation.isPresent());
    }

    @Test
    void verbPrinciplePartsAreParsedAndLoadedIntoModel() throws IOException {
        InflectionKey builder = new InflectionKey();
        String key = builder.buildFirstPrincipalPartKey();
        Inflection inflection  = getVerbLexemes().stream()
                .filter(l -> l.getLemma().equals("amo")) //note the lemma in wikt data doesn't have a macron
                .findFirst()
                .map(l -> l.getInflectionIndex().get(key))
                .orElse(null);
        Assertions.assertNotNull(inflection);
    }

    private void parseNounLexemes() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:testDataNoun.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            nounLexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexeme -> {
                if (lexeme.getInflections().get(0) instanceof Declension) {
                    nounLexemes.add(lexeme);
                }
            });
        }
    }

    private void parseVerbLexemes() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:testDataVerb.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            verbLexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexeme -> {
                if (lexeme.getInflections().get(0) instanceof Conjugation) {
                    verbLexemes.add(lexeme);
                }
            });

        }
    }
}
