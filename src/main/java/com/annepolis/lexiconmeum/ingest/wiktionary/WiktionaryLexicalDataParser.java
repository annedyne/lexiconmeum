package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Declension;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
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
import java.text.Normalizer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.FORM_OF;

@Component
class WiktionaryLexicalDataParser {

    static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataParser.class);
    private static final Marker NON_LEMMA = MarkerManager.getMarker("NON_LEMMA");

    static class LogMsg {
        private static final String SKIPPING_NON_LEMMA = "Skipping non-lemma entry for: {} {}";
        static final String SKIPPING_INVALID_FORM = "Skipping invalid form: {}";
        private static final String UNEXPECTED_INFLECTION_SOURCE = "Found an unexpected inflection source {} in form: {}";
        private static final String UNSUPPORTED_POS = "Unsupported partOfSpeech: {}";
        static final String FAILED_TO_BUILD = "Failed to build lexeme: {}";
        private static final String CANONICAL_NOT_FOUND = "Canonical Form Not Found: {}";
        private static final String JSONL_FORMAT_ERROR = "Check that JSONL is correctly formatted and not 'prettified'";

        private LogMsg() {} // Prevent instantiation
    }
    
    private static final Set<String> FORM_BLACKLIST = Set.of(
            "no-table-tags",
            "la-ndecl",
            "conjugation-1",
            "la-conj",
            "la-adecl",
            "two-termination",
            "sigmatic"
    );

    private final ObjectMapper mapper = new ObjectMapper();
    private ParseMode parseMode;

    private final LexicalTagResolver lexicalTagResolver;
    private final Map<PartOfSpeech, PartOfSpeechParser> partOfSpeechParsers;
    private final WiktionaryStagingService wiktionaryStagingService;

    public ParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ParseMode parseMode) {
        this.parseMode = parseMode;
    }

    WiktionaryLexicalDataParser(LexicalTagResolver lexicalTagResolver,
                                Map<PartOfSpeech, PartOfSpeechParser> partOfSpeechParsers,
                                WiktionaryStagingService wiktionaryStagingService
    ) {
        this.lexicalTagResolver = lexicalTagResolver;
        this.partOfSpeechParsers = partOfSpeechParsers;
        this.wiktionaryStagingService = wiktionaryStagingService;
    }

    public void parseJsonl(Reader reader, Consumer<Lexeme> lexemeConsumer) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = readJsonLine(br)) != null) {
            try {
                JsonNode root = mapper.readTree(line);
                buildLexeme(root).ifPresent(lexeme -> {
                    // Stage verbs, directly consume others
                    if (lexeme.getPartOfSpeech() == PartOfSpeech.VERB) {
                        wiktionaryStagingService.stageLexeme(lexeme);
                    } else {
                        lexemeConsumer.accept(lexeme);
                    }
                });
            } catch(JsonEOFException eofException) {
                logger.error(LogMsg.JSONL_FORMAT_ERROR, eofException);
                throw eofException;
            }
        }
    }

    String readJsonLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private boolean isValidLemmaEntry(JsonNode root, PartOfSpeech pos) {
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());

        if (!headTemplates.isArray() || headTemplates.isEmpty()) {
            return false;
        }

        // Run POS-specific validator if present
        PartOfSpeechParser validator = partOfSpeechParsers.get(pos);
        if (validator != null && !validator.validate(root)) {
            logger.debug(NON_LEMMA, "pos : {} ", pos::name );

            return false;
        }
        return true;
    }

    private Optional<Lexeme> buildLexeme(JsonNode root) {
        // Get primary keys
        String lemma = root.path(WORD.get()).asText();
        String posTag = root.path(PART_OF_SPEECH.get()).asText();
        String etymologyNumber = normalizeEtymologyNumber(root.path(ETYMOLOGY_NUMBER.get()).asText());

        // Validate POS against White-list
        Optional<PartOfSpeech> optionalPartOfSpeech = PartOfSpeech.resolveWithWarning(posTag, logger);
        if (optionalPartOfSpeech.isEmpty()) {
            logger.trace("Skipping unknown part of speech: {}", posTag);
            return Optional.empty();
        }
        PartOfSpeech partOfSpeech = optionalPartOfSpeech.get();

        // Only build valid lemma entries
        if (!isValidLemmaEntry(root, partOfSpeech)) {
            // Participles get staged and added to associated parent Lexeme at the end
            if (isParticipleEntry(root)) {
                handleParticipleEntry(root);
            }
            logger.trace(LogMsg.SKIPPING_NON_LEMMA, () -> posTag, () -> root.path(WORD.get()).asText());
            return Optional.empty();
        }

        // Initialize builder with necessary unique identifiers
        LexemeBuilder builder = new LexemeBuilder(lemma, partOfSpeech, etymologyNumber);

        // Add sense nodes
        addSenses(root.path(SENSES.get()), builder);
        PartOfSpeechParser partOfSpeechParser = partOfSpeechParsers.get(partOfSpeech);

        // Parse inflected forms and other POS-specific info
        return switch (partOfSpeech) {
            case ADJECTIVE, DETERMINER, PRONOUN -> buildLexemeWithForms(builder, root, this::addAdjectiveForms);
            case ADVERB, PREPOSITION, POSTPOSITION -> buildLexemeWithOutForms(builder);
            case CONJUNCTION -> buildLexemeWithForms(builder, root, this::findAndAddCanonicalForm);
            case NOUN -> buildLexemeWithForms(builder, root, this::addDeclensionForms);
            case VERB -> partOfSpeechParser.parsePartOfSpeech(builder, root);            default -> {
                logger.trace(LogMsg.UNSUPPORTED_POS, partOfSpeech);
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

    // Add canonical form to the builder
    private void addCanonicalForm(JsonNode formNode, LexemeBuilder lexemeBuilder) {
        try {
            for (JsonNode tag : formNode.path(TAGS.get())) {
                if(CANONICAL.name().equalsIgnoreCase(tag.asText())){
                    lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asText());
                    break;
                }
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
           logger.trace(LogMsg.SKIPPING_INVALID_FORM, ex.getMessage());
        }
    }

    // ---------------------------------- POS SPECIFIC FORM HANDLERS ---------------------------------- //

    // ---------------------------------- ADJECTIVE HANDLERS ---------------------------------- //

    private void addAdjectiveForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
            for (JsonNode formNode : formsNode) {
                if(isDeclensionForm(formNode)) {
                    try {
                        lexemeBuilder.addInflection(buildAgreement(formNode));
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                       logger.trace(LogMsg.SKIPPING_INVALID_FORM, ex.getMessage());
                    }
                } else {
                    addCanonicalForm(formNode, lexemeBuilder);
                }

            }
    }

    // Build adjective inflected form
    Agreement buildAgreement(JsonNode formNode) {
        Agreement.Builder builder = new Agreement.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            lexicalTagResolver.applyToInflection(builder, tag.asText(), logger);
        }

        return builder.build();
    }

    // ---------------------------------- NOUN HANDLERS ---------------------------------- //

    // Find and set canonical form and gender of nouns
    void setNounCanonicalFormAndGender(JsonNode formNode, LexemeBuilder lexemeBuilder){
        JsonNode tags = formNode.path(TAGS.get());
        for (int i = 0; i < tags.size(); i++) {
            //if first tag is CANONICAL then next is gender
            if (CANONICAL.name().equalsIgnoreCase(tags.get(i).asText()) && i + 1 < tags.size()) {
                lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asText());
                lexicalTagResolver.applyToLexeme(tags.get(i + 1).asText(), lexemeBuilder, logger);
            }
        }
    }

    private void addDeclensionForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (isDeclensionForm(formNode)) {
                buildDeclension(formNode, getParseMode()).ifPresent(lexemeBuilder::addInflection);
            } else {
                //this sets genders for nouns with gender-specific inflections (Ex: 1st & 2nd, not 3rd)
                setNounCanonicalFormAndGender(formNode, lexemeBuilder);
            }
        }
    }

    Optional<Declension> buildDeclension(JsonNode formNode, ParseMode mode) {
        Declension.Builder builder = new Declension.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            lexicalTagResolver.applyToInflection( builder, tag.asText(), logger);
        }

        return new SafeBuilder<>(DECLENSION.get(), builder::build).build(logger, mode);
    }

    // Filter out form nodes in black-list
    private boolean isDeclensionForm(JsonNode formNode){
        String formValue = formNode.path(FORM.get()).asText();

        if(FORM_BLACKLIST.contains(formValue)){
            return false;
        }
        if (!formNode.has(SOURCE.get())) {
            return false;
        }
        String source = formNode.path(SOURCE.get()).asText();

        // wrong inflection table header or pos in wiktionary data
       if( !DECLENSION.get().equalsIgnoreCase(source) &&
               !INFLECTION.get().equalsIgnoreCase(source)){
          logger.trace(LogMsg.UNEXPECTED_INFLECTION_SOURCE, source, formValue);
       }
        return true;
    }

    // ---------------------------------- PARTICIPLE HANDLERS ---------------------------------- //

    /**
     * Check if this verb entry is actually a participle form entry
     */
    protected boolean isParticipleEntry(JsonNode root) {
        // Must have head_templates
       JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        if (!headTemplates.isArray() || headTemplates.isEmpty()) {
            return false;
        }

        // Check template name
        String templateName = headTemplates.get(0).path(NAME.get()).asText("");
        return WiktionaryLexicalDataKeyWord.TEMPLATE_HEAD_PARTICPLE.get().equals(templateName);
    }
    /**
     * Handle a participle entry by staging it for later attachment
     */
    private void handleParticipleEntry(JsonNode root) {
        try {
            StagedParticipleData participleData = parseParticipleEntry(root);
            wiktionaryStagingService.stageParticiple(participleData);

        } catch (Exception e) {
            logger.error("Error parsing participle entry: {}", root.path(WORD.get()).asText(), e);
        }
    }

    /**
     * Parse a participle entry and return staged data.
     * This is called when processing a participle JSONL entry (not verb inflection data).
     */
    public StagedParticipleData parseParticipleEntry(JsonNode root) {
        String participleLemma = root.path(WORD.get()).asText();

        // If there is NO 'form_of' the tag, assuming participle is a GERUNDIVE,
        // and the key for parent Lexeme lookup is GERUNDIVE form
        String parentLemmaWithMacrons = participleLemma;
        String parentLemma = participleLemma;
        Conjugation.Builder conjBuilder = new Conjugation.Builder(parentLemmaWithMacrons);
        List<String> tags = new ArrayList<>();
        tags.add(GrammaticalVoice.PASSIVE.name().toLowerCase());
        tags.add(GrammaticalTense.FUTURE.name().toLowerCase());

        // Extract parent verb information from form_of if it exists,
        // and overwrite parent lemma
        // NB: Assuming the same tags for all senses for now
        JsonNode formOfArray = root.path(SENSES.get()).get(0).path(FORM_OF.get());
        if (formOfArray != null && !formOfArray.isEmpty()) {
            parentLemmaWithMacrons = formOfArray.get(0).path(WORD.get()).asText();
            parentLemma = removeMacrons(parentLemmaWithMacrons);

            JsonNode senseNode = root.path(SENSES.get()).get(0);
            tags = collectTags(senseNode);

        }

        lexicalTagResolver.applyAllToInflection(tags, conjBuilder, logger);
                // Build participle case inflections.
        Map<String, Agreement> inflections = parseParticipleInflections(root);

        return new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                conjBuilder.getVoice(),
                conjBuilder.getTense(),
                participleLemma,
                inflections
        );
    }

    String removeMacrons(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
    protected Map<String, Agreement> parseParticipleInflections(JsonNode root) {
        Map<String, Agreement> inflections = new HashMap<>();
        JsonNode formsArray = root.path(FORMS.get());

        for (JsonNode formNode : formsArray) {
            if (!isValidParticipleForm(formNode)) {
                continue;
            }

            Agreement agreement = buildAgreement(formNode);

            String key = InflectionKey.buildAgreementKey(agreement);
            inflections.put(key, agreement);
        }

        return inflections;
    }

    boolean isValidParticipleForm(JsonNode formNode){
        return formNode.path(SOURCE.get()).asText().equals(DECLENSION.get()) 
                && !FORM_BLACKLIST.contains(formNode.path(FORM.get()).asText());
    }

    private List<String> collectTags(JsonNode tagParentNode) {
        List<String> tags = new ArrayList<>();
        for (JsonNode tag : tagParentNode.path(TAGS.get())) {
            tags.add(tag.asText().toLowerCase());
        }
        return tags;
    }
}
