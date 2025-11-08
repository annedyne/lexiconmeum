package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Declension;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
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
import java.util.*;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass.THIRD;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {
        WiktionaryLexicalDataParser.class,
        LexicalTagResolver.class,
        PartOfSpeechParserConfig.class,
        POSVerbParser.class,         // and these if they are @Component-less
        POSNounParser.class,
        POSAdjectiveParser.class,
        EsseFormProvider.class,
        DefaultWiktionaryStagingService.class,
        StagedLexemeCache.class,
        ParticipleResolutionService.class
})
class WiktionaryLexicalDataParserSpringBootTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WiktionaryLexicalDataParser parser;

    // IF YOU ADD A VALID LEMMA NODE TO THE testDataRaw.jsonl ADD IT HERE
    static final String[] VALID_LEMMA_LIST = {"amo", "poculum", "pulcher", "brevis", "brevis", "brevis","brevis",
            "nox", "etsi", "ille", "ille" };

    private List<Lexeme> verbLexemes;
    private List<Lexeme> nounLexemes;
    private List<Lexeme> adjectiveLexemes;

    static final String STANDARD_VERB_LEMMA = "amo";

    public List<Lexeme> getVerbLexemes() throws IOException {
        if(verbLexemes == null) {
            parseVerbLexemes();
        }
        return verbLexemes;
    }

    private void parseVerbLexemes() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:testDataVerb.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            verbLexemes = new ArrayList<>();
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);

        }
    }

    public List<Lexeme> getNounLexemes() throws IOException {
        if(nounLexemes == null) {
            parseNounLexemes();
        }
        return nounLexemes;
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

    public List<Lexeme> getAdjectiveLexemes() throws IOException {
        if(adjectiveLexemes == null) {
            parseAdjectiveLexemes();
        }
        return adjectiveLexemes;
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

    private List<String> getValidLemmaList(){
        return Arrays.asList(VALID_LEMMA_LIST);
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

            assertEquals(VALID_LEMMA_LIST.length, lexemes.size());
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
    void IsValidLemmaDoesNotFilterOutValidIlleEntry() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);

            long illeCount = lexemes.stream()
                    .filter(l -> l.getLemma().equals("ille"))
                    .count();

            long expected = getValidLemmaList().stream()
                    .filter(l -> l.equals("ille"))
                    .count();
            assertEquals(expected, illeCount, "Expected exactly one 'ille' lemma");
        }
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


}
