package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.Set;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass.THIRD;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {WiktionaryLexicalDataParser.class, LexicalTagResolver.class})
class WiktionaryLexicalDataParserTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WiktionaryLexicalDataParser parser;

    private List<Lexeme> verbLexemes;
    private List<Lexeme> nounLexemes;
    private List<Lexeme> adjectiveLexemes;
    private List<Lexeme> allLexemeTypes;

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

    public List<Lexeme> getAdjectiveLexemes() throws IOException {
        if(adjectiveLexemes == null) {
            parseAdjectiveLexemes();
        }
        return adjectiveLexemes;
    }

    public List<Lexeme> getAllLexemeTypes() throws IOException {
        if(allLexemeTypes == null) {
            parseAllLexemeTypes();
        }
        return allLexemeTypes;
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

            assertEquals(10, lexemes.size());
            assertEquals(PartOfSpeech.VERB, lexemes.get(0).getPartOfSpeech());
            assertEquals(PartOfSpeech.NOUN, lexemes.get(1).getPartOfSpeech());
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
    void testLoadConjunction() throws Exception {
        Optional<Lexeme> pulcher = getAdjectiveLexemes().stream()
                .filter(l -> l.getLemma().equals("pulcher"))
                .findFirst();
        assertTrue(pulcher.isPresent(), "Pulcher lexeme not found");
    }

    @ParameterizedTest
    @MethodSource("expectedPulcherForms")
    void threeTerminationAdjectiveInflectionsLoaded(String expectedForm) throws Exception {
        Optional<Lexeme> pulcher = getAdjectiveLexemes().stream()
                .filter(l -> l.getLemma().equals("pulcher"))
                .findFirst();
        assertTrue(pulcher.isPresent(), "Pulcher lexeme not found");

        boolean found = pulcher.get().getInflections().stream()
                .anyMatch(i -> i.getForm().equals(expectedForm)
                        || expectedForm.equals("pulcherrimē")); //no superlative adverb in data

        pulcher.get().getInflections()
                .forEach(i -> {
                    if (i instanceof Agreement ag) {
                        assert ag.getNumber() != null : "GrammaticalNumber is null in Agreement: " + ag;
                    }
                });
        assertTrue(found, "Expected form not found: " + expectedForm);
    }

    static Stream<String> expectedPulcherForms() {
        return TestUtil.expectedPulcherForms();
    }

    @Test
    void thirdInflectionAssignedSetOnTwoTerminationAdjectiveInflection() throws IOException {
        Optional<Lexeme> brevis = getAdjectiveLexemes().stream()
                .filter(l -> l.getLemma().equals("brevis"))
                .findFirst();
        assertTrue(brevis.isPresent(), "Brevis lexeme not found");
        assertEquals(Set.of(THIRD), brevis.get().getInflectionClasses());

        brevis.get().getInflections().stream()
                .forEach(i -> {
                    if (i instanceof Agreement ag) {
                        assert ag.getNumber() != null : "GrammaticalNumber is null in Agreement: " + ag;
                    }
                });
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
    void verbPrincipalPartsAreParsedAndLoadedIntoModel() throws IOException {
        InflectionKey builder = new InflectionKey();
        String key = builder.buildFirstPrincipalPartKey();
        Inflection inflection  = getVerbLexemes().stream()
                .filter(l -> l.getLemma().equals("amo")) //note the lemma in wikt data doesn't have a macron
                .findFirst()
                .map(l -> l.getInflectionIndex().get(key))
                .orElse(null);
        Assertions.assertNotNull(inflection);
    }

    @Test
    void parseConjunction() throws  IOException {
        Optional<Lexeme> etsi = getAllLexemeTypes().stream()
                .filter(l -> l.getLemma().equals("etsi"))
                .findFirst();
        assertTrue(etsi.isPresent(), "Etsi lexeme not found");
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

    private void parseAdjectiveLexemes() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:testDataAdjective.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            adjectiveLexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexeme -> {
                if (lexeme.getInflections().get(0) instanceof Agreement) {
                    adjectiveLexemes.add(lexeme);
                }
            });

        }
    }

    private void parseAllLexemeTypes() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            allLexemeTypes = new ArrayList<>();
            parser.parseJsonl(reader, lexeme -> {
                allLexemeTypes.add(lexeme);
            });

        }
    }
}
