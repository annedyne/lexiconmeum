package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeFixtureFactory;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.inflection.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass.THIRD;
import static org.junit.jupiter.api.Assertions.*;

class WiktionaryLexicalDataParserTest {

    private WiktionaryLexicalDataParser parser;
    private WiktionaryStagingServiceStub stagingServiceStub;

    // IF YOU ADD A VALID LEMMA NODE TO THE testDataRaw.jsonl ADD IT HERE
    static final String[] VALID_UNSTAGED_LEXEME_LIST = { "amo", "poculum", "brevis", "brevis", "brevis",
            "nox", "etsi", "ille", "ille" };

    static final String[] VALID_STAGED_LEXEME_LIST = {"amo", "brevis", "pulcher", "pulso", "sequor"};
    static final String[] NON_LEXEME_STAGED_LIST = {"amans", "sequendus", "amandus", "brevissimus", "pulchrior", "pulcherrimus"};
    private List<Lexeme> verbLexemes;
    private List<Lexeme> adjectiveLexemes;


    static final String STANDARD_VERB_LEMMA = "amo";
    private static final LexicalTagResolver LEXICAL_TAG_RESOLVER = new LexicalTagResolver();
    private static final ParserSupport PARSER_SUPPORT = new ParserSupport(LEXICAL_TAG_RESOLVER, ParseMode.STRICT);


    @BeforeEach
    void setUp() {
        // Create real dependencies
        EsseFormProvider esseFormProvider = new EsseFormProvider();

        POSConjunctionParser conjunctionParser = new POSConjunctionParser(PARSER_SUPPORT);
        POSVerbParser verbParser = new POSVerbParser(esseFormProvider, PARSER_SUPPORT);
        POSNounParser nounParser = new POSNounParser(PARSER_SUPPORT);
        POSAdjectiveParser adjectiveParser = new POSAdjectiveParser(PARSER_SUPPORT);
        POSParticipleParser participleParser = new POSParticipleParser(PARSER_SUPPORT);
        POSNonInflectedFormParser nonInflectedFormParser = new POSNonInflectedFormParser(PARSER_SUPPORT);

        Map<POSParserKey, PartOfSpeechParser> posParsers = new EnumMap<>(POSParserKey.class);
        posParsers.put(POSParserKey.DETERMINER, adjectiveParser);
        posParsers.put(POSParserKey.PRONOUN, adjectiveParser);
        posParsers.put(POSParserKey.ADJECTIVE_POSITIVE, adjectiveParser);
        posParsers.put(POSParserKey.ADJECTIVE_COMPARATIVE, adjectiveParser);
        posParsers.put(POSParserKey.ADJECTIVE_SUPERLATIVE, adjectiveParser);

        posParsers.put(POSParserKey.CONJUNCTION, conjunctionParser);
        posParsers.put(POSParserKey.VERB, verbParser);
        posParsers.put(POSParserKey.NOUN, nounParser);
        posParsers.put(POSParserKey.PARTICIPLE, participleParser);

        posParsers.put(POSParserKey.ADVERB, nonInflectedFormParser);
        posParsers.put(POSParserKey.PREPOSITION, nonInflectedFormParser);
        posParsers.put(POSParserKey.POSTPOSITION, nonInflectedFormParser);

        // Create test stub for staging service
        stagingServiceStub = new WiktionaryStagingServiceStub();

        // Create parser with stub
        parser = new WiktionaryLexicalDataParser(
                posParsers,
                stagingServiceStub
        );
    }

    static class WiktionaryStagingServiceStub implements WiktionaryStagingService {

        public List<Lexeme> stagedLexemes = new ArrayList<>();
        public List<LinkableData> stagedParticiples = new ArrayList<>();


        @Override
        public void stageLexeme(Lexeme lexeme) {
            stagedLexemes.add(lexeme);
        }

        @Override
        public void stageLinkableData(LinkableData participleData) {
            stagedParticiples.add(participleData);
        }

        @Override
        public DataLinkingService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) {
            return null;
        }
    }


    public List<Lexeme> getVerbLexemes() throws IOException {
        if(verbLexemes == null) {
            parseVerbLexemes();
        }
        return verbLexemes;
    }

    private void parseVerbLexemes() throws IOException {
        Resource resource = new ClassPathResource("testDataVerb.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {

            parser.parseJsonl(reader, lexeme -> {});

            // Get verbs from staging service
            verbLexemes = stagingServiceStub.stagedLexemes;
        }
    }

    public List<Lexeme> getAdjectiveLexemes() throws IOException {
        if(adjectiveLexemes == null) {
            parseAdjectiveLexemes();
        }
        return adjectiveLexemes;
    }

    private void parseAdjectiveLexemes() throws IOException {
        Resource resource = new ClassPathResource("testDataAdjective.jsonl");

        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            adjectiveLexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexeme -> {});
            adjectiveLexemes = stagingServiceStub.stagedLexemes;
        }
    }

    private List<Lexeme> nounLexemes;

    public List<Lexeme> getNounLexemes() throws IOException {
        if(nounLexemes == null) {
            parseNounLexemes();
        }
        return nounLexemes;
    }

    private void parseNounLexemes() throws IOException {
        Resource resource = new ClassPathResource("testDataNoun.jsonl");

        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            nounLexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexeme -> {
                if (lexeme.getInflections().get(0) instanceof Declension) {
                    nounLexemes.add(lexeme);
                }
            });
        }
    }


    @Test
    void resourceExists() {
        Resource resource = new ClassPathResource("testDataRaw.jsonl");
        assertTrue(resource.exists(), "Expected testDataRaw.jsonl to be present on the classpath");
    }

    @Test
    void JsonlFileParsesWithoutError() {
        Resource resource = new ClassPathResource("testDataRaw.jsonl");
        assertDoesNotThrow(() -> {
            try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                BufferedReader br = new BufferedReader(reader);
                parser.readJsonLine(br);
            }
        });
    }

    @Test
    void testLoadJsonFile() throws Exception {

        Resource resource = new ClassPathResource("testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> validNonVerbLexemes = new ArrayList<>();

            parser.parseJsonl(reader, validNonVerbLexemes::add);

            int totalCount = stagingServiceStub.stagedLexemes.size() + stagingServiceStub.stagedParticiples.size() + validNonVerbLexemes.size();
            assertEquals(VALID_UNSTAGED_LEXEME_LIST.length + VALID_STAGED_LEXEME_LIST.length + NON_LEXEME_STAGED_LIST.length, totalCount);
        }
    }

    @Test
    void verbsAreStagedParticiplesAreConsumed() throws Exception {
        Resource resource = new ClassPathResource("testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> consumedLexemes = new ArrayList<>();
            parser.parseJsonl(reader, consumedLexemes::add);

            // Verify verb "amo" was staged
            Lexeme amoStaged = stagingServiceStub.stagedLexemes.stream()
                    .filter(l -> "amo".equals(l.getLemma()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("'amo' not found in staging"));


            // Verify noun "amo" was consumed
            Lexeme amoConsumed = consumedLexemes.stream()
                    .filter(l -> "amo".equals(l.getLemma()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("'amo' not found in consumed"));

            // test that these are different Lexemes with the same lemma
            assertNotEquals(amoConsumed.getId(), amoStaged.getId());
        }
    }

    @Test
    void IsValidLemmaFiltersOutInvalidPulsoEntry() throws Exception {
        Resource resource = new ClassPathResource("testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> allLexemes = new ArrayList<>();
            parser.parseJsonl(reader, allLexemes::add);
            allLexemes.addAll(stagingServiceStub.stagedLexemes);

            long pulsoCount = allLexemes.stream()
                    .filter(l -> l.getLemma().equals("pulso"))
                    .count();

            assertEquals(1, pulsoCount, "Expected exactly one 'pulso' lemma");
        }
    }

    @Test
    void verbPrincipalPartsAreParsedAndLoadedIntoModel() throws IOException {
        InflectionKey builder = new InflectionKey();
        String key = builder.buildFirstPrincipalPartKey();
        Inflection inflection  = getVerbLexemes().stream()
                .filter(l -> l.getLemma().equals(STANDARD_VERB_LEMMA)) //note the lemma in wikt data doesn't have a macron
                .findFirst()
                .map(l -> l.getInflectionIndex().get(key))
                .orElse(null);
        Assertions.assertNotNull(inflection);
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
        return LexemeFixtureFactory.expectedPulcherForms();
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