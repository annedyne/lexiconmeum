package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class JsonTestDataManager {
    // Shared instance for "Spring-free" unit tests
    public static final JsonTestDataManager INSTANCE = new JsonTestDataManager();

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, List<JsonNode>> cache = new HashMap<>();
    private final Map<POSParserKey, PartOfSpeechParser> parserRegistry = new EnumMap<>(POSParserKey.class);
    private final LexicalTagResolver lexicalTagResolver = new LexicalTagResolver();
    private final ParserSupport parserSupport = new ParserSupport(lexicalTagResolver, ParseMode.STRICT);

     private JsonTestDataManager() {
        // Explicitly wire the dependencies as Spring would
         EsseFormProvider esseFormProvider = new EsseFormProvider();
         POSVerbParser verbParser = new POSVerbParser(esseFormProvider,parserSupport);
         POSNounParser nounParser = new POSNounParser(parserSupport);
         POSAdjectiveParser adjectiveParser = new POSAdjectiveParser(parserSupport);

         parserRegistry.put(POSParserKey.VERB, verbParser);
         parserRegistry.put(POSParserKey.NOUN, nounParser);
         parserRegistry.put(POSParserKey.ADJECTIVE_POSITIVE, adjectiveParser);
         parserRegistry.put(POSParserKey.ADJECTIVE_COMPARATIVE, adjectiveParser);
         parserRegistry.put(POSParserKey.ADJECTIVE_SUPERLATIVE, adjectiveParser);
         parserRegistry.put(POSParserKey.DETERMINER, adjectiveParser);
         parserRegistry.put(POSParserKey.PRONOUN, adjectiveParser);
    }

    /**
     * Replaces the synthetic getNewTestNounLexeme by parsing a real JSON entry via the actual parser.
     */
    public Lexeme getParsedNounLexeme(String word, String filename) throws IOException {
        JsonNode root = getRealNode(word, filename);

        WiktionaryStagingServiceStub stagingStub = getStagingServiceStub();

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parserRegistry, stagingStub);
        AtomicReference<Lexeme> captured = new AtomicReference<>();
        Consumer<Lexeme> consumer = captured::set;

        parser.processJson(root, consumer);

        return captured.get();
    }

    private WiktionaryLexicalDataParser getLexicalDataParser(Map<POSParserKey, PartOfSpeechParser> parsers, WiktionaryStagingService stagingStub) {
        return new WiktionaryLexicalDataParser(
                lexicalTagResolver,
                parsers,
                new POSParticipleParser(lexicalTagResolver),
                stagingStub,
                parserSupport
        );
    }

    private WiktionaryStagingServiceStub getStagingServiceStub() {

        return new WiktionaryStagingServiceStub();
    }

    // Capture staged lexemes so tests can retrieve what processors staged.
    class WiktionaryStagingServiceStub implements WiktionaryStagingService {
        private final List<Lexeme> stagedLexemes = new ArrayList<>();

        @Override
        public void stageLexeme(Lexeme lexeme) {
            stagedLexemes.add(lexeme);
        }

        public Optional<Lexeme> getLastStagedLexeme() {
            return stagedLexemes.isEmpty()
                    ? Optional.empty()
                    : Optional.of(stagedLexemes.get(stagedLexemes.size() - 1));
        }

        @Override
        public void stageParticiple(StagedParticipleData participleData) {}

        @Override
        public ParticipleResolutionService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) { return null;}
    }

    /**
     * Replaces the synthetic getNewTestVerbLexeme by parsing a real JSON entry via the actual parser.
     */
    public Lexeme getParsedVerbLexeme(String word, String filename) throws IOException {
        JsonNode root = getRealNode(word, filename);

        WiktionaryStagingServiceStub stagingStub = getStagingServiceStub();

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parserRegistry, stagingStub);

        Consumer<Lexeme> consumer = lexeme -> {};
        parser.processJson(root, consumer);

        return stagingStub.getLastStagedLexeme()
                .orElseThrow(() -> new IllegalStateException("Parser failed to stage a Lexeme for verb: " + word));
    }

    /**
     * Finds a specific node by its "word" property across one or more files.
     */
    public JsonNode getRealNode(String word, String... filenames) throws IOException {
        for (String file : filenames) {
            List<JsonNode> nodes = loadFile(file);
            Optional<JsonNode> match = nodes.stream()
                .filter(n -> n.path("word").asText().equalsIgnoreCase(word))
                .findFirst();
            if (match.isPresent()) return match.get();
        }
        throw new IllegalArgumentException("Word '" + word + "' not found in provided test files.");
    }

    private List<JsonNode> loadFile(String filename) throws IOException {
        if (cache.containsKey(filename)) return cache.get(filename);

        List<JsonNode> nodes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(filename).getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                nodes.add(mapper.readTree(line));
            }
        }
        cache.put(filename, nodes);
        return nodes;
    }


}
