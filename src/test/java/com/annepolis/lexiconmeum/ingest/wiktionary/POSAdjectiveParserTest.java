package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.WORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Directly exercises the pronoun triage in {@link POSAdjectiveParser}:
 * suppletive plural children are staged as linkables, their parents are staged
 * as lexemes, and any other pronoun is processed immediately.
 */
class POSAdjectiveParserTest {

    private static final ParserSupport PARSER_SUPPORT =
            new ParserSupport(new LexicalTagResolver(), ParseMode.STRICT);

    private final POSAdjectiveParser parser = new POSAdjectiveParser(PARSER_SUPPORT);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, JsonNode> rootsByWord;

    @BeforeEach
    void loadFixtureRoots() throws IOException {
        rootsByWord = new HashMap<>();
        Resource resource = new ClassPathResource("testDataPronoun.jsonl");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                JsonNode root = objectMapper.readTree(line);
                rootsByWord.put(root.path(WORD.get()).asString(), root);
            }
        }
    }

    // Runs the parser for the given fixture word as a pronoun and captures where it routed.
    private CapturingStagingService routePronoun(String word) {
        CapturingStagingService staging = new CapturingStagingService();
        ParsedResultProcessor processor = parser.parsePartOfSpeech(rootsByWord.get(word), POSParserKey.PRONOUN);
        processor.process(staging.consumed::add, staging);
        return staging;
    }

    @Test
    void childPronounIsStagedAsLinkableToParent() {
        CapturingStagingService staging = routePronoun("nos");

        assertEquals(1, staging.linkables.size());
        assertTrue(staging.stagedLexemes.isEmpty());
        assertTrue(staging.consumed.isEmpty());

        StagedPronounData pronounData = assertInstanceOf(StagedPronounData.class, staging.linkables.get(0));
        assertEquals("nos", pronounData.getLemma());
        assertEquals("ego", pronounData.getLinkingLemma());
    }

    @Test
    void parentPronounIsStagedAsLexeme() {
        CapturingStagingService staging = routePronoun("ego");

        assertEquals(1, staging.stagedLexemes.size());
        assertEquals("ego", staging.stagedLexemes.get(0).getLemma());
        assertTrue(staging.linkables.isEmpty());
        assertTrue(staging.consumed.isEmpty());
    }

    @Test
    void nonSuppletivePronounIsProcessedImmediately() {
        CapturingStagingService staging = routePronoun("ille");

        assertEquals(1, staging.consumed.size());
        assertEquals("ille", staging.consumed.get(0).getLemma());
        assertTrue(staging.stagedLexemes.isEmpty());
        assertTrue(staging.linkables.isEmpty());
    }

    private static class CapturingStagingService implements WiktionaryStagingService {
        final List<Lexeme> stagedLexemes = new ArrayList<>();
        final List<LinkableData> linkables = new ArrayList<>();
        final List<Lexeme> consumed = new ArrayList<>();

        @Override
        public void stageLexeme(Lexeme lexeme) {
            stagedLexemes.add(lexeme);
        }

        @Override
        public void stageLinkableData(LinkableData dataToLink) {
            linkables.add(dataToLink);
        }

        @Override
        public DataLinkingService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) {
            return null;
        }
    }
}