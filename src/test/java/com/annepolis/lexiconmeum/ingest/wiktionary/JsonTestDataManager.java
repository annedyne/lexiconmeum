package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
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

    private JsonTestDataManager() {
        // Explicitly wire the dependencies as Spring would
        EsseFormProvider esseFormProvider = new EsseFormProvider();
        LexicalTagResolver lexicalTagResolver = new LexicalTagResolver();
        ParserSupport parserSupport = new ParserSupport(lexicalTagResolver, ParseMode.STRICT);
        POSVerbParser verbParser = new POSVerbParser(esseFormProvider, parserSupport);
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
     * Loads the file for the given filename, finds the given noun JSON data,
     * parses the JSON data with the application's parser,
     * and returns it as a noun Lexeme object.
     *
     * @param word the word to be parsed and matched in the JSON file
     * @param filename the name of the file containing JSON data for parsing
     * @return a Lexeme object representing the parsed noun lexeme, or null if the lexeme could not be captured
     * @throws IOException if an error occurs when reading from the JSON file
     */
    public Lexeme getParsedNounLexeme(String word, String filename) throws IOException {
        JsonNode root = getRealNode(word, PartOfSpeech.NOUN, filename);

        WiktionaryStagingServiceStub stagingStub = getStagingServiceStub();

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parserRegistry, stagingStub);
        AtomicReference<Lexeme> captured = new AtomicReference<>();
        Consumer<Lexeme> consumer = captured::set;

        parser.processJson(root, consumer);

        return captured.get();
    }

    private WiktionaryLexicalDataParser getLexicalDataParser(Map<POSParserKey, PartOfSpeechParser> parsers, WiktionaryStagingService stagingStub) {
        return new WiktionaryLexicalDataParser(
                parsers,
                stagingStub
        );
    }

    private WiktionaryStagingServiceStub getStagingServiceStub() {
        return new WiktionaryStagingServiceStub();
    }

    // Capture staged lexemes so tests can retrieve what processors staged.
    static class WiktionaryStagingServiceStub implements WiktionaryStagingService {
        private final List<Lexeme> stagedLexemes = new ArrayList<>();

        @Override
        public void stageLexeme(Lexeme lexeme) {
            stagedLexemes.add(lexeme);
        }

        @Override
        public void stageLinkableData(LinkableData linkableData) {
            // not needed for testing
        }

        public Optional<Lexeme> getLastStagedLexeme() {
            return stagedLexemes.isEmpty()
                    ? Optional.empty()
                    : Optional.of(stagedLexemes.get(stagedLexemes.size() - 1));
        }

        @Override
        public DataLinkingService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) { return null;}
    }

    /**
     * Loads the file for the given filename, finds the given noun JSON data,
     * parses the JSON data with the application's parser,
     * and returns it as a verb Lexeme object.
     *
     * @param word the word to be parsed and matched in the JSON file
     * @param filename the name of the file containing JSON data for parsing
     * @return a Lexeme object representing the parsed verb lexeme, or null if the lexeme could not be captured
     * @throws IOException if an error occurs when reading from the JSON file
     */
    public Lexeme getParsedVerbLexeme(String word, String filename) throws IOException {
        JsonNode root = getRealNode(word, PartOfSpeech.VERB ,filename);

        return getStagedLexeme(word, root);
    }

    private Lexeme getStagedLexeme(String word, JsonNode root) {
        WiktionaryStagingServiceStub stagingStub = getStagingServiceStub();

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parserRegistry, stagingStub);

        Consumer<Lexeme> consumer = lexeme -> {};
        parser.processJson(root, consumer);

        return stagingStub.getLastStagedLexeme()
                .orElseThrow(() -> new IllegalStateException("Parser failed to stage a Lexeme for: " + word));
    }

    /**
     * Finds a specific node by its "word" and Part of Speech (pos) property across one or more files.
     */
    public JsonNode  getRealNode(String word, PartOfSpeech pos, String filename, String... additionalFilenames) throws IOException {
        // Process the required filename
        List<JsonNode> nodes = loadFile(filename);
        Optional<JsonNode> match = nodes.stream()
                .filter(n -> n.path("word").asText().equalsIgnoreCase(word) && n.path("pos").asText().equals(pos.getTag() ))
                .findFirst();
        if (match.isPresent()) return match.get();

        // process any additional filenames
        for (String file : additionalFilenames) {
            nodes = loadFile(file);
            match = nodes.stream()
                    .filter(n -> n.path("word").asText().equalsIgnoreCase(word) && n.path("pos").asText().equals(pos.getTag()) )
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
