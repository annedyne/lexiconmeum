package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class WiktionaryLexicalDataParserTest {

    private WiktionaryLexicalDataParser parser;
    private WiktionaryStagingServiceStub stagingServiceStub;

    // IF YOU ADD A VALID LEMMA NODE TO THE testDataRaw.jsonl ADD IT HERE
    static final String[] VALID_NON_VERB_LEMMA_LIST = { "amo", "poculum", "pulchrior", "pulcher", "brevis", "brevis", "brevis","brevis",
            "nox", "etsi", "ille", "ille" };

    static final String[] VALID_VERB_LEMMA_LIST = {"amo", "pulso", "sequor"};
    private List<Lexeme> verbLexemes;

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
        public List<StagedParticipleData> stagedParticiples = new ArrayList<>();


        @Override
        public void stageLexeme(Lexeme lexeme) {
            stagedLexemes.add(lexeme);
        }

        @Override
        public void stageParticiple(StagedParticipleData participleData) {
            stagedParticiples.add(participleData);
        }

        @Override
        public ParticipleResolutionService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) {
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

            int totalCount = stagingServiceStub.stagedLexemes.size() + validNonVerbLexemes.size();
            assertEquals(VALID_NON_VERB_LEMMA_LIST.length + VALID_VERB_LEMMA_LIST.length, totalCount);
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


}