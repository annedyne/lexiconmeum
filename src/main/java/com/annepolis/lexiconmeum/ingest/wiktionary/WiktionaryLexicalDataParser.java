package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Declension;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

@Component
class WiktionaryLexicalDataParser {

    static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataParser.class);

    private static class LogMsg {
        private static final String SKIPPING_NON_LEMMA = "Skipping non-lemma entry for: {}";
        private static final String SKIPPING_INVALID_FORM = "Skipping invalid form: {}";
        private static final String UNEXPECTED_INFLECTION_SOURCE = "Found an unexpected inflection source {} in form: {}";
        private static final String UNSUPPORTED_POS = "Unsupported partOfSpeech: {}";
        private static final String FAILED_TO_BUILD = "Failed to build lexeme: {}";
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

    private static final Set<String> TAG_BLACKLIST = Set.of(
            "sigmatic"
    );

    private final ObjectMapper mapper = new ObjectMapper();
    private ParseMode parseMode;

    // Inject the resolver instead of instantiating it
    private final LexicalTagResolver lexicalTagResolver;

    public ParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ParseMode parseMode) {
        this.parseMode = parseMode;
    }

    WiktionaryLexicalDataParser(LexicalTagResolver lexicalTagResolver) {
        this.lexicalTagResolver = lexicalTagResolver;
    }

    public void parseJsonl(Reader reader, Consumer<Lexeme> consumer) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = readJsonLine(br)) != null) {
            try {
                JsonNode root = mapper.readTree(line);
                buildLexeme(root).ifPresent(consumer);
            } catch(JsonEOFException eofException) {
                logger.error(LogMsg.JSONL_FORMAT_ERROR, eofException);
                throw eofException;
            }
        }
    }

    String readJsonLine(BufferedReader br) throws IOException {
        return br.readLine();
    }


    private boolean isValidLemmaEntry(JsonNode root) {
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());

        if (!headTemplates.isArray() || headTemplates.isEmpty()) {
            return false;
        }

        // Get the template name from the first head template
        String templateName = headTemplates.get(0).path(NAME.get()).asText("");

        // Filter out generic "head" templates and form-specific templates
        return (!HEAD.get().equals(templateName) && !templateName.contains(FORM.get()));
    }

    private Optional<Lexeme> buildLexeme(JsonNode root) {

        // Only build valid lemma entries
        if (!isValidLemmaEntry(root)) {
            logger.trace(LogMsg.SKIPPING_NON_LEMMA, () -> root.path(WORD.get()).asText());
            return Optional.empty();
        }

        // Get primary keys
        String lemma = root.path(WORD.get()).asText();
        String posTag = root.path(PART_OF_SPEECH.get()).asText();
        String etymologyNumber = normalizeEtymologyNumber(root.path(ETYMOLOGY_NUMBER.get()).asText());

        // Validate POS against White-list
        Optional<PartOfSpeech> optionalPartOfSpeech = PartOfSpeech.resolveWithWarning(posTag, logger);
        if (optionalPartOfSpeech.isEmpty()) {
            return Optional.empty();
        }
        PartOfSpeech partOfSpeech = optionalPartOfSpeech.get();

        // Initialize builder with necessary unique identifiers
        LexemeBuilder builder = new LexemeBuilder(lemma, partOfSpeech, etymologyNumber);

        // Add sense nodes
        addSenses(root.path(SENSES.get()), builder);

        // Parse inflected forms and other POS-specific info
        return switch (partOfSpeech) {
            case ADJECTIVE, DETERMINER, PRONOUN -> buildLexemeWithForms(builder, root, this::addAdjectiveForms);
            case ADVERB, PREPOSITION, POSTPOSITION -> buildLexemeWithOutForms(builder);
            case CONJUNCTION -> buildLexemeWithForms(builder, root, this::findAndAddCanonicalForm);
            case NOUN -> buildLexemeWithForms(builder, root, this::addDeclensionForms);
            case VERB -> buildLexemeWithForms(builder, root, this::addConjugationForms);
            default -> {
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
            lexicalTagResolver.applyToInflection(tag.asText(), builder, logger);
        }

        return builder.build();
    }

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
            lexicalTagResolver.applyToInflection(tag.asText(), builder, logger);
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

    private void addConjugationForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (isConjugationForm(formNode)) {
                try {
                    buildConjugation(formNode).ifPresent(lexemeBuilder::addInflection);
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    logger.trace(LogMsg.SKIPPING_INVALID_FORM, ex.getMessage());
                }
            } else {
                addCanonicalForm(formNode, lexemeBuilder);
            }
        }
    }
    Optional<Conjugation> buildConjugation(JsonNode formNode){
        if (hasBlacklistedTag(formNode)) {
            return Optional.empty();
        }

        Conjugation.Builder builder = new Conjugation.Builder(formNode.path(FORM.get()).asText());
        List<String> tags = collectTags(formNode);
        
        replaceCompoundTense(tags);
        
        for (String tag : tags){
            lexicalTagResolver.applyToInflection(tag, builder, logger);
        }

        return Optional.of(builder.build());
    }

    private boolean hasBlacklistedTag(JsonNode formNode) {
        for (JsonNode tagNode : formNode.path(TAGS.get())) {
            if (TAG_BLACKLIST.contains(tagNode.asText())) {
                return true;
            }
        }
        return false;
    }

    private List<String> collectTags(JsonNode formNode) {
        List<String> tags = new ArrayList<>();
        for (JsonNode tag : formNode.path(TAGS.get())) {
            tags.add(tag.asText().toLowerCase());
        }
        return tags;
    }

    // Replace two separate tense tags with compound
    private void replaceCompoundTense(List<String> tags) {
        String future = GrammaticalTense.FUTURE.name().toLowerCase();
        String perfect = GrammaticalTense.PERFECT.name().toLowerCase();
        
        if (tags.contains(future) && tags.contains(perfect)) {
            tags.remove(future);
            tags.remove(perfect);
            tags.add(GrammaticalTense.FUTURE_PERFECT.name().toLowerCase());
        }
    }

    // Filter out form nodes in black-list
    private boolean isConjugationForm(JsonNode formNode){
        String formValue = formNode.path(FORM.get()).asText();

        return CONJUGATION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                && !FORM_BLACKLIST.contains(formValue)
                && !formValue.contains("+"); //for passive of compound tenses, wiktionary
                                             // doesn't include person so excluding for now
    }
}
