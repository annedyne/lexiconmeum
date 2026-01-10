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
import java.util.function.Consumer;

public class JsonTestDataManager {
    // Shared instance for "Spring-free" unit tests
    public static final JsonTestDataManager INSTANCE = new JsonTestDataManager();

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, List<JsonNode>> cache = new HashMap<>();
    private final LexicalTagResolver tagResolver;

    private JsonTestDataManager() {
        // Explicitly wire the dependencies as Spring would
        this.tagResolver = new LexicalTagResolver();
    }

    /**
     * Replaces the synthetic getNewTestNounLexeme by parsing a real JSON entry via the actual parser.
     */
    public Lexeme getParsedNounLexeme(String word, String filename) throws IOException {
        JsonNode root = getRealNode(word, filename);

        // Setup the parser with necessary dependencies
        Map<PartOfSpeech, PartOfSpeechParser> parsers = new EnumMap<>(PartOfSpeech.class);
        parsers.put(PartOfSpeech.NOUN, new POSNounParser());

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parsers, getStagingServiceStub());
        parser.setParseMode(ParseMode.STRICT);

        return parser.buildLexeme(root)
                .orElseThrow(() -> new IllegalStateException("Parser failed to produce a Lexeme for word: " + word));
    }

    private WiktionaryLexicalDataParser getLexicalDataParser(Map<PartOfSpeech, PartOfSpeechParser> parsers, WiktionaryStagingService stagingStub) {
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

        Map<PartOfSpeech, PartOfSpeechParser> parsers = new EnumMap<>(PartOfSpeech.class);
        parsers.put(PartOfSpeech.VERB, new POSVerbParser(tagResolver, new EsseFormProvider()));

        WiktionaryStagingService stagingStub = getStagingServiceStub();

        WiktionaryLexicalDataParser parser = getLexicalDataParser(parsers, stagingStub);
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
