package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionBuilder;
import com.annepolis.lexiconmeum.shared.util.Utilities;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

/**
 * Helper lass for common POSParser-specific parsing tasks
 */
@Component
public class ParserSupport {

    private final LexicalTagResolver lexicalTagResolver;
    private final ParseMode parseMode;
    static class LogMsg {

        static final String FAILED_TO_BUILD = "Failed to build lexeme: {}";
        static final String SKIPPING_INVALID_FORM = "Skipping invalid form: {}";
        static final String UNEXPECTED_INFLECTION_SOURCE = "Found an unexpected inflection source {} in form: {}";
        private LogMsg() {} // Prevent instantiation
    }

    public ParserSupport(LexicalTagResolver lexicalTagResolver, ParseMode parseMode) {
        this.lexicalTagResolver = lexicalTagResolver;
        this.parseMode = parseMode;
    }

    ParseMode getParseMode() {
        return parseMode;
    }


    Optional<LexemeBuilder> initLexemeBuilderFromRoot(JsonNode root, Logger logger){
       return extractPrimaryKeyData(root, logger).map(primaryKeyData -> new LexemeBuilder(
               primaryKeyData.lemma(),
               primaryKeyData.partOfSpeech(),
               primaryKeyData.etymologyNumber()
       ));
    }

    public Optional<POSPrimaryKeyData> extractPrimaryKeyData(JsonNode root, Logger logger){
        // Get primary keys
        String lemma = root.path(WORD.get()).asString();
        String posTag = root.path(PART_OF_SPEECH.get()).asString();
        String etymologyNumber = normalizeEtymologyNumber(root.path(ETYMOLOGY_NUMBER.get()).asString());

        Optional<PartOfSpeech> optionalPartOfSpeech = PartOfSpeech.resolveWithWarning(posTag, logger);
        if (optionalPartOfSpeech.isEmpty()) {
            logger.trace("Skipping unknown part of speech: {}", posTag);
            return Optional.empty();
        }
        PartOfSpeech partOfSpeech = optionalPartOfSpeech.get();

        return Optional.of(new POSPrimaryKeyData(lemma, partOfSpeech, etymologyNumber));
    }

    // Default to etymologyNumber of 1 to ensure identifier consistency
    public static String normalizeEtymologyNumber(String ety) {
        return ety == null || ety.isBlank() ? "1" : ety;
    }

    public boolean isValidFormNode(JsonNode formNode, String inflectionType) {
        return formNode.path(SOURCE.get()).asString().equals(inflectionType)
                && !ParserConstants.COMMON_FORM_BLACKLIST.contains(formNode.path(FORM.get()).asString());
    }

    public boolean isValidForm(JsonNode formNode){
       return !ParserConstants.COMMON_FORM_BLACKLIST.contains(formNode.path(FORM.get()).asString());
    }

    /**
     * Standardized way to extract canonical forms from the common 'forms' array
     */
    public void extractCanonicalForms(JsonNode root, LexemeBuilder builder) {
        JsonNode formsNode = root.path(FORMS.get());
        if (!formsNode.isArray()) return;

        for (JsonNode formNode : formsNode) {
            JsonNode tags = formNode.path(TAGS.get());
            if (tags.isArray()) {
                for (JsonNode tag : tags) {
                    if (CANONICAL.name().equalsIgnoreCase(tag.asString())) {
                        builder.addCanonicalForm(formNode.path(FORM.get()).asString());
                    }
                }
            }
        }
    }

    /**
     * Checks a single node for canonical tags and adds to builder.
     * Use this inside existing loops in specialized parsers to save cycles.
     */
    public void addCanonicalForm(JsonNode formNode, LexemeBuilder lexemeBuilder) {
        JsonNode tags = formNode.path(TAGS.get());
        if (!tags.isArray()) return;

        for (JsonNode tag : tags) {
            if (CANONICAL.name().equalsIgnoreCase(tag.asString())) {
                lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asString());
                break;
            }
        }
    }

    // Build sense nodes and add to builder
    void addSenses(JsonNode sensesNode, LexemeBuilder lexemeBuilder, Logger logger) {
        if (sensesNode.isArray()) {
            for (JsonNode senseNode : sensesNode) {
                lexemeBuilder.addSense(buildSense(senseNode, lexemeBuilder, logger));
            }
        }
    }

    private Sense buildSense(JsonNode senseNode, LexemeBuilder lexemeBuilder, Logger logger) {
        Sense.Builder builder = new Sense.Builder();
        JsonNode tags = senseNode.path(TAGS.get());
        if(tags.isArray() && !tags.isEmpty()){
            for(JsonNode tag : tags){
                // Route all sense-level tags through the facade
                lexicalTagResolver.applyToLexeme(tag.asString(), lexemeBuilder, logger);
            }
        }

        JsonNode glosses = senseNode.path(GLOSSES.get());
        if (glosses.isArray() && !glosses.isEmpty()) {

            for(JsonNode gloss: glosses){
                builder.addGloss(gloss.asString());
            }
        }
        return builder.build();
    }


    public void applyToInflection(InflectionBuilder builder, String inflectionTag, Logger logger){
        lexicalTagResolver.applyToInflection(builder, inflectionTag, logger);
    }

    public void applyAllToInflection(Iterable<String> tags, InflectionBuilder builder, Logger logger){
        lexicalTagResolver.applyAllToInflection(tags, builder, logger);
    }

    public void applyToLexeme(String tag, LexemeBuilder builder, Logger logger) {
        lexicalTagResolver.applyToLexeme(tag, builder, logger);
    }

    List<String> collectTags(JsonNode tagParentNode) {
        List<String> tags = new ArrayList<>();
        for (JsonNode tag : tagParentNode.path(TAGS.get())) {
            tags.add(tag.asString().toLowerCase());
        }
        return tags;
    }

    String normalizeDiacritics(String lemmaWithMacrons){
        return Utilities.normalizeDiacritics(lemmaWithMacrons);
    }
}
