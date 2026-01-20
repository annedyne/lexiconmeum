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
import java.util.function.Consumer;

public class JsonTestDataManager {
    // Shared instance for "Spring-free" unit tests
    public static final JsonTestDataManager INSTANCE = new JsonTestDataManager();

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, List<JsonNode>> cache = new HashMap<>();
    private final LexicalTagResolver tagResolver;
    private final Map<POSParserKey, PartOfSpeechParser> parserRegistry = new EnumMap<>(POSParserKey.class);

    private JsonTestDataManager() {
        // Explicitly wire the dependencies as Spring would
        this.tagResolver = new LexicalTagResolver();
        
        // Initialize Registry
        POSVerbParser verbParser = new POSVerbParser(tagResolver, new EsseFormProvider());
        POSNounParser nounParser = new POSNounParser();
        POSAdjectiveParser adjectiveParser = new POSAdjectiveParser();

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

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parserRegistry, getStagingServiceStub());
        parser.setParseMode(ParseMode.STRICT);

        return parser.buildLexeme(root)
                .orElseThrow(() -> new IllegalStateException("Parser failed to produce a Lexeme for word: " + word));
    }

    private WiktionaryLexicalDataParser getLexicalDataParser(Map<POSParserKey, PartOfSpeechParser> parsers, WiktionaryStagingService stagingStub) {
        return new WiktionaryLexicalDataParser(
                tagResolver,
                parsers,
                new POSParticipleParser(tagResolver),
                stagingStub
        );
    }

    private WiktionaryStagingService getStagingServiceStub() {
        return new WiktionaryStagingService() {
            @Override public void stageLexeme(Lexeme lexeme) {}
            @Override public void stageParticiple(StagedParticipleData participleData) {}
            @Override public ParticipleResolutionService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) { return null; }
        };
    }

    /**
     * Replaces the synthetic getNewTestVerbLexeme by parsing a real JSON entry via the actual parser.
     */
    public Lexeme getParsedVerbLexeme(String word, String filename) throws IOException {
        JsonNode root = getRealNode(word, filename);

        WiktionaryStagingService stagingStub = getStagingServiceStub();

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parserRegistry, stagingStub);
        parser.setParseMode(ParseMode.STRICT);

        return parser.buildLexeme(root)
                .orElseThrow(() -> new IllegalStateException("Parser failed to produce a Lexeme for verb: " + word));
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
