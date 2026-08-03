package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

@Component
class WiktionaryLexicalDataParser {

    static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataParser.class);
    private static final Marker PARSER_DELEGATION_ISSUE = MarkerManager.getMarker("PARSER_DELEGATION_ISSUE");

    static class LogMsg {
        private static final String MISSING_NODES = "{} not found";

        private LogMsg() {} // Prevent instantiation
    }
    
    private final Map<POSParserKey, PartOfSpeechParser> partOfSpeechParserRegistry;
    private final WiktionaryStagingService wiktionaryStagingService;


    WiktionaryLexicalDataParser(
                                Map<POSParserKey, PartOfSpeechParser> partOfSpeechParserRegistry,
                                WiktionaryStagingService wiktionaryStagingService
    ) {
        this.partOfSpeechParserRegistry = partOfSpeechParserRegistry;
        this.wiktionaryStagingService = wiktionaryStagingService;
    }

    void processJson(JsonNode root, Consumer<Lexeme> lexemeConsumer){
        // Unified resolution: if we have a key, it's a potential lemma
        Optional<POSParserKey> optionalParserKey = deriveParserKeyFromRoot(root);
        // Delegate to specialized parser if available
        if(optionalParserKey.isPresent()) {
            POSParserKey parserKey = optionalParserKey.get();
            PartOfSpeechParser specializedParser = partOfSpeechParserRegistry.get(parserKey);
            parseLexicalDataEntry(specializedParser, root, parserKey).process(lexemeConsumer, wiktionaryStagingService);
        } else {
            logger.trace("Skipping unknown head-template");
        }
    }

    ParsedResultProcessor parseLexicalDataEntry(PartOfSpeechParser partOfSpeechParser, JsonNode root, POSParserKey parserKey){

        return partOfSpeechParser.parsePartOfSpeech(root,parserKey );
    }

    Optional<POSParserKey> deriveParserKeyFromRoot(JsonNode root) {
        return extractHeadTemplateNameFromRoot(root).flatMap(POSParserKey::fromHeadTemplateName);
    }

    public Optional<String> extractHeadTemplateNameFromRoot(JsonNode root) {
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        if (!headTemplates.isArray() || headTemplates.isEmpty()) {
            logger.trace(PARSER_DELEGATION_ISSUE, LogMsg.MISSING_NODES, HEAD_TEMPLATES.name());
            return Optional.empty();
        }

        JsonNode firstTemplate = headTemplates.get(0);
        String name = firstTemplate.path(NAME.get()).asString("");

        if(firstTemplate.path(NAME.get()).asString("").isEmpty()){
            logger.trace(PARSER_DELEGATION_ISSUE, LogMsg.MISSING_NODES, HEAD_TEMPLATES.name() + NAME.name());
            return Optional.empty();
        } else if ("head".equalsIgnoreCase(name)) {
            String arg2 = firstTemplate.path("args").path("2").asString();
            if (arg2.isEmpty()) {
                logger.trace(PARSER_DELEGATION_ISSUE, LogMsg.MISSING_NODES, HEAD_TEMPLATES.name() + ARGS.name() );
                return Optional.empty();
            } else {
               return Optional.of(arg2);
            }
        }

        return Optional.of(name);
    }


}
