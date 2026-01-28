package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

@Component
class WiktionaryLexicalDataParser {

    static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataParser.class);
    private static final Marker NON_LEMMA = MarkerManager.getMarker("NON_LEMMA");
    private static final Marker PARSER_DELEGATION_ISSUE = MarkerManager.getMarker("PARSER_DELEGATION_ISSUE");

    static class LogMsg {
        private static final String SKIPPING_NON_LEMMA = "Skipping non-lemma entry for: {} {}";
        static final String SKIPPING_INVALID_FORM = "Skipping invalid form: {}";
        static final String UNEXPECTED_INFLECTION_SOURCE = "Found an unexpected inflection source {} in form: {}";
        private static final String UNSUPPORTED_POS = "Unsupported partOfSpeech: {}";
        static final String FAILED_TO_BUILD = "Failed to build lexeme: {}";
        private static final String CANONICAL_NOT_FOUND = "Canonical Form Not Found: {}";
        private static final String JSONL_FORMAT_ERROR = "Check that JSONL is correctly formatted and not 'prettified'";
        private static final String MISSING_NODES = "{} not found";

        private LogMsg() {} // Prevent instantiation
    }
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final LexicalTagResolver lexicalTagResolver;
    private final Map<POSParserKey, PartOfSpeechParser> partOfSpeechParserRegistry;
    private final WiktionaryStagingService wiktionaryStagingService;
    private final POSParticipleParser participleParser;
    private final ParserSupport parserSupport;


    WiktionaryLexicalDataParser(LexicalTagResolver lexicalTagResolver,
                                Map<POSParserKey, PartOfSpeechParser> partOfSpeechParserRegistry,
                                POSParticipleParser participleParser,
                                WiktionaryStagingService wiktionaryStagingService, ParserSupport parserSupport
    ) {
        this.lexicalTagResolver = lexicalTagResolver;
        this.partOfSpeechParserRegistry = partOfSpeechParserRegistry;
        this.participleParser = participleParser;
        this.wiktionaryStagingService = wiktionaryStagingService;
        this.parserSupport = parserSupport;
    }

    public void parseJsonl(Reader reader, Consumer<Lexeme> lexemeConsumer) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = readJsonLine(br)) != null) {
            try {
                JsonNode root = mapper.readTree(line);
                processJson(root, lexemeConsumer);

            }  catch(JsonEOFException eofException) {
                logger.error(LogMsg.JSONL_FORMAT_ERROR, eofException);
                throw eofException;
            }
        }
    }

    String readJsonLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    void processJson(JsonNode root, Consumer<Lexeme> lexemeConsumer){
        // Unified resolution: if we have a key, it's a potential lemma
        Optional<POSParserKey> optionalParserKey = deriveParserKeyFromRoot(root);
        // Delegate to specialized parser if available
        if(optionalParserKey.isPresent()) {
            POSParserKey parserKey = optionalParserKey.get();
            PartOfSpeechParser specializedParser = partOfSpeechParserRegistry.get(parserKey);
            if (specializedParser.isActive()) {
                parseLexicalDataEntry(specializedParser, root).process(lexemeConsumer, wiktionaryStagingService);
            } else buildLexeme(root, parserKey).ifPresent(lexemeConsumer);
        } else {
            logger.trace("Skipping unknown head-template");
        }
    }

    ParsedResultProcessor parseLexicalDataEntry(PartOfSpeechParser partOfSpeechParser, JsonNode root){

        return partOfSpeechParser.parsePartOfSpeech(root);
    }


    private Optional<POSParserKey> deriveParserKeyFromRoot(JsonNode root) {
        return extractHeadTemplateNameFromRoot(root).flatMap(POSParserKey::fromHeadTemplateName);
    }

    public Optional<String> extractHeadTemplateNameFromRoot(JsonNode root) {
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        if (!headTemplates.isArray() || headTemplates.isEmpty()) {
            logger.trace(PARSER_DELEGATION_ISSUE, LogMsg.MISSING_NODES, HEAD_TEMPLATES.name());
            return Optional.empty();
        }

        JsonNode firstTemplate = headTemplates.get(0);
        String name = firstTemplate.path(NAME.get()).asText("");

        if(firstTemplate.path(NAME.get()).asText("").isEmpty()){
            logger.trace(PARSER_DELEGATION_ISSUE, LogMsg.MISSING_NODES, HEAD_TEMPLATES.name() + NAME.name());
            return Optional.empty();
        } else if ("head".equalsIgnoreCase(name)) {
            String arg2 = firstTemplate.path("args").path("2").asText();
            if (arg2.isEmpty()) {
                logger.trace(PARSER_DELEGATION_ISSUE, LogMsg.MISSING_NODES, HEAD_TEMPLATES.name() + ARGS.name() );
                return Optional.empty();
            } else {
               return Optional.of(arg2);
            }
        }

        return Optional.of(name);
    }


    Optional<Lexeme> buildLexeme(JsonNode root, POSParserKey posParserKey) {

        // Only build valid lemma entries
        if (posParserKey == POSParserKey.PARTICIPLE) {
            // Participles get staged and added to associated parent Lexeme at the end
            handleParticipleEntry(root);

            logger.trace(LogMsg.SKIPPING_NON_LEMMA, POSParserKey.PARTICIPLE::name, () -> root.path(WORD.get()).asText());
            // participles are staged so return empty
            return Optional.empty();
        }


        // Initialize builder with necessary unique identifiers
        Optional<POSPrimaryKeyData> optionalPrimaryKeyData = parserSupport.extractPrimaryKeyData(root, logger);
        if(optionalPrimaryKeyData.isEmpty()){
            return Optional.empty();
        }
        POSPrimaryKeyData primaryKeyData = optionalPrimaryKeyData.get();
        LexemeBuilder builder = new LexemeBuilder(primaryKeyData.lemma(), primaryKeyData.partOfSpeech(), primaryKeyData.etymologyNumber());

        // Add sense nodes
        addSenses(root.path(SENSES.get()), builder);


        // Parse inflected forms and other POS-specific info
        return switch (primaryKeyData.partOfSpeech()) {
            case ADVERB, PREPOSITION, POSTPOSITION -> buildLexemeWithOutForms(builder);
            case CONJUNCTION -> buildLexemeWithForms(builder, root, this::findAndAddCanonicalForm);
            default -> {
                logger.trace(LogMsg.UNSUPPORTED_POS, primaryKeyData.partOfSpeech());
                yield Optional.empty();
            }
        };
    }

    // Build sense nodes and add to builder
    private void addSenses(JsonNode sensesNode, LexemeBuilder lexemeBuilder) {
        if (sensesNode.isArray()) {
            for (JsonNode senseNode : sensesNode) {
                lexemeBuilder.addSense(buildSense(senseNode, lexemeBuilder));
            }
        }
    }

    private Sense buildSense(JsonNode senseNode, LexemeBuilder lexemeBuilder) {
        Sense.Builder builder = new Sense.Builder();
        JsonNode tags = senseNode.path(TAGS.get());
        if(tags.isArray() && !tags.isEmpty()){
            for(JsonNode tag : tags){
                // Route all sense-level tags through the facade
                lexicalTagResolver.applyToLexeme(tag.asText(), lexemeBuilder, logger);
            }
        }

        JsonNode glosses = senseNode.path(GLOSSES.get());
        if (glosses.isArray() && !glosses.isEmpty()) {

            for(JsonNode gloss: glosses){
                builder.addGloss(gloss.asText());
            }
        }
        return builder.build();
    }

    // Default to etymologyNumber of 1 to ensure identifier consistency
    public static String normalizeEtymologyNumber(String ety) {
        return ety == null || ety.isBlank() ? "1" : ety;
    }

    // Build non-inflected forms
    private Optional<Lexeme> buildLexemeWithOutForms(LexemeBuilder builder){
        try {
            return Optional.of(builder.build());
        } catch (Exception ex) {
            logger.warn(LogMsg.FAILED_TO_BUILD, ex.getMessage());
            return Optional.empty();
        }
    }



    // Build inflected forms
    private Optional<Lexeme> buildLexemeWithForms(
            LexemeBuilder builder,
            JsonNode root,
            BiConsumer<JsonNode, LexemeBuilder> addForms
    ) {
        addForms.accept(root.path(FORMS.get()), builder);
        try {
            return Optional.of(builder.build());
        } catch (Exception ex) {
            logger.warn(LogMsg.FAILED_TO_BUILD, ex.getMessage());
            return Optional.empty();
        }
    }

    // Find canonical form (same as lemma but with enclitics) and add to builder
    private void findAndAddCanonicalForm(JsonNode formsNode, LexemeBuilder lexemeBuilder){
        for (JsonNode formNode : formsNode) {
            try {
                for (JsonNode tag : formNode.path(TAGS.get())) {
                    if(CANONICAL.name().equalsIgnoreCase(tag.asText())){
                        lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asText());
                        break;
                    }
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
               logger.trace(LogMsg.CANONICAL_NOT_FOUND, ex.getMessage());
            }
        }
    }

    /**
     * Handle a participle entry by staging it for later attachment
     */
    private void handleParticipleEntry(JsonNode root) {
        try {
            participleParser.parseParticipleEntry(root)
                    .ifPresent(wiktionaryStagingService::stageParticiple);

        } catch (Exception e) {
            logger.error("Error parsing participle entry: {}", root.path(WORD.get()).asText(), e);
        }
    }
}
