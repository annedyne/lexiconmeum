package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.WORD;
import static org.junit.jupiter.api.Assertions.*;

class WiktionaryLexicalDataParserTest {

    private WiktionaryLexicalDataParser parser;
    private WiktionaryStagingServiceStub stagingServiceStub;

    // IF YOU ADD A VALID LEMMA NODE TO THE testDataRaw.jsonl ADD IT HERE
    static final String[] VALID_NON_VERB_LEMMA_LIST = { "amo", "poculum", "pulcher", "brevis", "brevis", "brevis","brevis",
            "nox", "etsi", "ille", "ille" };

    static final String[] VALID_VERB_LEMMA_LIST = {"amo", "pulso", "sequor"};
    private List<Lexeme> verbLexemes;

    static final String STANDARD_VERB_LEMMA = "amo";

    @BeforeEach
    void setUp() {
        // Create real dependencies
        LexicalTagResolver lexicalTagResolver = new LexicalTagResolver();

        EsseFormProvider esseFormProvider = new EsseFormProvider();

        Map<PartOfSpeech, PartOfSpeechParser> partOfSpeechParsers = new HashMap<>();
        partOfSpeechParsers.put(PartOfSpeech.VERB, new POSVerbParser(lexicalTagResolver, esseFormProvider));
        partOfSpeechParsers.put(PartOfSpeech.NOUN, new POSNounParser());
        partOfSpeechParsers.put(PartOfSpeech.ADJECTIVE, new POSAdjectiveParser());

        // Create test stub for staging service
        stagingServiceStub = new WiktionaryStagingServiceStub();

        // Create parser with stub
        parser = new WiktionaryLexicalDataParser(
                lexicalTagResolver,
                partOfSpeechParsers,
                stagingServiceStub
        );
        parser.setParseMode(ParseMode.STRICT);
    }

    class WiktionaryStagingServiceStub implements WiktionaryStagingService {

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

    @Test
    void isParticipleEntryReturnsTrueGivenParticipleRoot() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amans"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Particple 'amans' not found"));

        boolean isParticiple = parser.isParticipleEntry(root);

        assertTrue(isParticiple);
    }

    @Test
    void isParticipleEntryReturnsFalseGivenAVerbRoot() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Verb 'amo' not found"));

        boolean isParticiple = parser.isParticipleEntry(root);

        assertFalse(isParticiple);
    }

    @Test
    void parseParticipleEntryGeneratesExpectedStagedParticipleData() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amans"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participle 'amans' not found"));

        StagedParticipleData data = parser.parseParticipleEntry(root);

        assertEquals("amans", data.getParticipleLemma());
        assertEquals("amo", data.getParentLemma());
        assertEquals("ACTIVE|PRESENT", data.getParticipleKey());
        assertEquals("amō", data.getParentLemmaWithMacrons());
    }

    @Test
    void parseParticipleInflectionsGeneratesExpectedInflections() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amans"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participle 'amans' not found"));

        Map<String, Agreement>  inflections = parser.parseParticipleInflections(root);

        //Spot Check is fine, since props set via Agreement tagMapping
        assertEquals(16, inflections.size());
        Agreement ablativePlural = inflections.get("ABLATIVE|PLURAL|MASCULINE|FEMININE|NEUTER|POSITIVE");
        assertEquals(3,ablativePlural.getGenders().size() );
        assertEquals("amantibus",ablativePlural.getForm() );
        assertEquals(GrammaticalCase.ABLATIVE,ablativePlural.getGrammaticalCase() );
    }

    @Test
    void removeMacronsNormalizesStringAsExpected(){
        String normalized = parser.removeMacrons("āēīōū");
        assertEquals("aeiou", normalized);
    }

}