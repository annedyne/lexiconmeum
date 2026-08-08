package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeFixtureFactory;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.annepolis.lexiconmeum.testsupport.TestSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass.THIRD;
import static org.junit.jupiter.api.Assertions.*;

class WiktionaryLexicalDataParserTest {

    private WiktionaryLexicalDataParser parser;
    private WiktionaryStagingServiceStub stagingServiceStub;
    private JsonlResourceReader jsonlResourceReader;

    // IF YOU ADD A VALID LEMMA NODE TO THE testDataRaw.jsonl ADD IT HERE
    static final String[] VALID_UNSTAGED_LEXEME_LIST = {"sum", "amo", "poculum", "brevis", "brevis", "brevis",
            "nox", "etsi", "ille", "ille" };

    static final String[] VALID_STAGED_LEXEME_LIST = {"sum", "amo", "brevis", "pulcher", "pulso", "sequor"};
    static final String[] NON_LEXEME_STAGED_LIST = {"doctus", "doctior", "doctissimus","futurus", "amans", "sequendus", "amandus", "brevissimus", "pulchrior", "pulcherrimus"};
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
        POSVerbParser verbParser = new POSVerbParser(new CompoundInflectionGenerator(esseFormProvider), PARSER_SUPPORT);
        POSNounParser nounParser = new POSNounParser(PARSER_SUPPORT);
        POSAdjectiveParser adjectiveParser = new POSAdjectiveParser(PARSER_SUPPORT);
        POSParticipleParser participleParser = new POSParticipleParser(PARSER_SUPPORT, new CompoundInflectionGenerator(esseFormProvider));
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
        jsonlResourceReader = new JsonlResourceReader(new ObjectMapper());

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
        parseResource("testDataVerb.jsonl", lexeme -> {});

        // Get verbs from staging service
        verbLexemes = stagingServiceStub.stagedLexemes;
    }

    public List<Lexeme> getAdjectiveLexemes() throws IOException {
        if(adjectiveLexemes == null) {
            parseAdjectiveLexemes();
        }
        return adjectiveLexemes;
    }

    private void parseAdjectiveLexemes() throws IOException {
        adjectiveLexemes = new ArrayList<>();
        parseResource("testDataAdjective.jsonl", lexeme -> {});
        adjectiveLexemes = stagingServiceStub.stagedLexemes;
    }

    private List<Lexeme> nounLexemes;

    public List<Lexeme> getNounLexemes() throws IOException {
        if(nounLexemes == null) {
            parseNounLexemes();
        }
        return nounLexemes;
    }

    private void parseNounLexemes() throws IOException {
        nounLexemes = new ArrayList<>();
        parseResource("testDataNoun.jsonl", lexeme -> {
            if (lexeme.getInflections().get(0) instanceof Declension) {
                nounLexemes.add(lexeme);
            }
        });
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
            jsonlResourceReader.read(resource, node -> {});
        });
    }

    @Test
    void testLoadJsonFile() throws Exception {

        List<Lexeme> validNonVerbLexemes = new ArrayList<>();

        parseResource("testDataRaw.jsonl", validNonVerbLexemes::add);

        int totalCount = stagingServiceStub.stagedLexemes.size() + stagingServiceStub.stagedParticiples.size() + validNonVerbLexemes.size();
        assertEquals(VALID_UNSTAGED_LEXEME_LIST.length + VALID_STAGED_LEXEME_LIST.length + NON_LEXEME_STAGED_LIST.length, totalCount);
    }

    @Test
    void verbsAreStagedParticiplesAreConsumed() throws Exception {
        List<Lexeme> consumedLexemes = new ArrayList<>();
        parseResource("testDataRaw.jsonl", consumedLexemes::add);

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

    @Test
    void IsValidLemmaFiltersOutInvalidPulsoEntry() throws Exception {
        List<Lexeme> allLexemes = new ArrayList<>();
        parseResource("testDataRaw.jsonl", allLexemes::add);
        allLexemes.addAll(stagingServiceStub.stagedLexemes);

        long pulsoCount = allLexemes.stream()
                .filter(l -> l.getLemma().equals("pulso"))
                .count();

        assertEquals(1, pulsoCount, "Expected exactly one 'pulso' lemma");
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
        getVerbLexemes().stream().filter(lexeme -> lexeme.getLemma().equals("amo"))
                .findFirst()
                .map(l -> l.getSenses().stream()
                        .filter(s -> s.getGloss().contains("to love"))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Missing gloss 'to love'")));
    }

    @SuppressWarnings("java:S2699")
    @Test
    void rawGlossesAreParsedAndPopulated() throws Exception {
        getAdjectiveLexemes().stream()
                .filter(lexeme -> lexeme.getLemma().equals("magnus"))
                .findFirst()
                .map(l -> l.getSenses().stream()
                        .filter(s -> s.getGloss().contains("great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)"))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Missing raw gloss for magnus")));
    }

    @Test
    void parserMapsFutureAndPerfectTagsToFuturePerfectTense() throws Exception {
        Optional<Inflection> tenseTag = getVerbLexemes().stream().filter(lexeme -> lexeme.getLemma().equals("amo"))
                .findFirst()
                .map(l -> l.getInflections().stream()
                    .filter(g -> g.getForm().equals("amāverō"))
                    .findFirst()
                .orElseThrow(() -> new AssertionError("Missing future-perfect test form")));
        assertTrue(tenseTag.isPresent());
        assertEquals(GrammaticalTense.FUTURE_PERFECT, ((Conjugation) tenseTag.get()).getTense());
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

        brevis.get().getInflections()
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

    @Test
    void verbEsseIsParsedAndStaged() throws IOException {
        List<Lexeme> consumedLexemes = new ArrayList<>();
        parseResource("testDataRaw.jsonl", consumedLexemes::add);

        // Verify verb "sum" was staged
        Lexeme sumStaged = stagingServiceStub.stagedLexemes.stream()
                .filter(l -> "sum".equals(l.getLemma()) && PartOfSpeech.VERB == l.getPartOfSpeech())
                .findFirst()
                .orElseThrow(() -> new AssertionError("'sum' not found in staging"));

        assertEquals(PartOfSpeech.VERB, sumStaged.getPartOfSpeech());
        String key = InflectionKey.joinConjugationParts(GrammaticalVoice.ACTIVE, GrammaticalMood.SUBJUNCTIVE, GrammaticalTense.PERFECT, GrammaticalPerson.FIRST, GrammaticalNumber.SINGULAR);
        assertEquals("fuerim", sumStaged.getInflectionIndex().get(key).getForm());

    }

    private void parseResource(String filename, Consumer<Lexeme> lexemeConsumer) throws IOException {
        jsonlResourceReader.read(new ClassPathResource(filename), node -> parser.processJson(node, lexemeConsumer));
    }

    // The reflexive pronoun 'sui' is tagged as a non-lemma "pronoun form"; it is flagged by
    // name so parser-key derivation rescues it as PRONOUN rather than skipping it.
    @Test
    void reflexivePronounSuiDerivesPronounParserKey() throws Exception {
        JsonNode suiPronoun = TestSupport.getInstance().getJsonTestDataManager()
                .getRealNode("sui", PartOfSpeech.PRONOUN, "testDataPronoun.jsonl");

        assertEquals(Optional.of(POSParserKey.PRONOUN), parser.deriveParserKeyFromRoot(suiPronoun));
    }

    // The noun-form and verb-form 'sui' entries on the same page are not "pronoun form",
    // so the rescue does not apply and they derive no key.
    @Test
    void suiNounAndVerbFormEntriesDeriveNoParserKey() throws Exception {
        JsonTestDataManager data = TestSupport.getInstance().getJsonTestDataManager();

        assertEquals(Optional.empty(),
                parser.deriveParserKeyFromRoot(data.getRealNode("sui", PartOfSpeech.NOUN, "testDataPronoun.jsonl")));
        assertEquals(Optional.empty(),
                parser.deriveParserKeyFromRoot(data.getRealNode("sui", PartOfSpeech.VERB, "testDataPronoun.jsonl")));
    }
}
