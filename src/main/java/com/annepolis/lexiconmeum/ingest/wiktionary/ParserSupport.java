package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataParser.normalizeEtymologyNumber;

/**
 * Helper lass for common POSParser-specific parsing tasks
 */
@Component
public class ParserSupport {

    private final LexicalTagResolver lexicalTagResolver;
    private final ParseMode parseMode;

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
        String lemma = root.path(WORD.get()).asText();
        String posTag = root.path(PART_OF_SPEECH.get()).asText();
        String etymologyNumber = normalizeEtymologyNumber(root.path(ETYMOLOGY_NUMBER.get()).asText());

        Optional<PartOfSpeech> optionalPartOfSpeech = PartOfSpeech.resolveWithWarning(posTag, logger);
        if (optionalPartOfSpeech.isEmpty()) {
            logger.trace("Skipping unknown part of speech: {}", posTag);
            return Optional.empty();
        }
        PartOfSpeech partOfSpeech = optionalPartOfSpeech.get();

        return Optional.of(new POSPrimaryKeyData(lemma, partOfSpeech, etymologyNumber));
    }

    public String getHeadTemplateName(JsonNode root) {
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        return headTemplates.get(0).path(NAME.get()).asText("");
    }


    public boolean isValidFormNode(JsonNode formNode, String inflectionType) {
        return formNode.path(SOURCE.get()).asText().equals(inflectionType)
                && !ParserConstants.COMMON_FORM_BLACKLIST.contains(formNode.path(FORM.get()).asText());
    }


    /**
     * Checks a single node for canonical tags and adds to builder.
     * Use this inside existing loops in specialized parsers to save cycles.
     */
    public void addCanonicalForm(JsonNode formNode, LexemeBuilder lexemeBuilder) {
        JsonNode tags = formNode.path(TAGS.get());
        if (!tags.isArray()) return;

        for (JsonNode tag : tags) {
            if (CANONICAL.name().equalsIgnoreCase(tag.asText())) {
                lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asText());
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


    public void applyToInflection(InflectionBuilder builder, String inflectionTag, Logger logger){
        lexicalTagResolver.applyToInflection(builder, inflectionTag, logger);
    }


    public void applyToLexeme(String tag, LexemeBuilder builder, Logger logger) {
        lexicalTagResolver.applyToLexeme(tag, builder, logger);
    }
}
