package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalParticipleTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import com.annepolis.lexiconmeum.shared.util.Utilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.FORM_OF;

@Component
public class POSParticipleParser implements PartOfSpeechParser {

    ParserSupport parserSupport;

    public POSParticipleParser(ParserSupport parserSupport){
        this.parserSupport = parserSupport;
    }

    static final Logger logger = LogManager.getLogger(POSParticipleParser.class);

    @Override
    public ParsedResultProcessor parsePartOfSpeech(JsonNode root, POSParserKey parserKey) {

        // Build the participle and return it wrapped in the appropriate processor, or EMPTY if no result
        return parseParticipleEntry(root).map( participleData -> (ParsedResultProcessor) (
                lexemeConsumer,
                stagingService) -> stagingService.stageLinkableData(participleData)
        )
        .orElse(ParsedResultProcessor.EMPTY);
    }

     Optional<StagedParticipleData> parseParticipleEntry(JsonNode root) {

        String participleLemma = root.path(WORD.get()).asString();

        // Build the agreement/declension attributes - case inflections.
        List<Participle> inflections = parseParticipleInflections(root);

        // Extract parent verb information from sense node form_of tag if it exists,
        JsonNode senseNode = getFormOfSenseNodeOrFirst(root);

        JsonNode formOfArray = senseNode.path(FORM_OF.get());

        if (formOfArray != null && !formOfArray.isEmpty()) {
            return deriveParticipleDataFromSenseNodeFormOfTag(participleLemma, senseNode, inflections);
        } else {
            //If form_of does not exist, pull data from etymology_text
            String etymologyText = root.path(ETYMOLOGY_TEXT.get()).asString();
            return deriveParticipleDataFromEtymologyText(participleLemma, etymologyText, inflections);
        }
    }

    // return either the sense node that has a form-of
    // tag or if none, do, the first in the array.
    JsonNode getFormOfSenseNodeOrFirst(JsonNode root){
        JsonNode sensesNode = root.path(SENSES.get());

        if (sensesNode.isArray()) {
            for (JsonNode node : sensesNode) {
                JsonNode formOfArray = node.path(FORM_OF.get());
                if(formOfArray.isArray() && !formOfArray.isEmpty()){
                    return node;
                }
            }
        }
        // default to first
        return sensesNode.get(0);
    }

    Optional<StagedParticipleData> deriveParticipleDataFromSenseNodeFormOfTag(
            String participleLemma,
            JsonNode senseNode,
            List<Participle> inflections
    ) {
        // NB: Assuming the same tags for all senses for now
        JsonNode formOfArray = senseNode.path(FORM_OF.get());
        String parentLemmaWithMacrons = formOfArray.get(0).path(WORD.get()).asString();
        String parentLemma = removeMacrons(parentLemmaWithMacrons);

        Conjugation.Builder conjBuilder = new Conjugation.Builder(parentLemmaWithMacrons);

        List<String> senseTags = parserSupport.collectTags(senseNode);
        senseTags = resolveParticipleTenseTags(senseTags);

        // resolve any conjugation attributes of participle (tense and voice)
        parserSupport.applyAllToInflection(senseTags, conjBuilder, logger);

        ParticipleDeclensionSet.Builder participleSetBuilder = new ParticipleDeclensionSet.Builder(
                conjBuilder.getVoice(),
                conjBuilder.getTense(),
                participleLemma
        );
        participleSetBuilder.addInflections(inflections);

        return participleSetBuilder.buildOptional()
                .map(participleSet -> new StagedParticipleData(
                        parentLemma,
                        parentLemmaWithMacrons,
                        participleSet
                ));
    }

    Optional<StagedParticipleData> deriveParticipleDataFromEtymologyText(String participleLemma, String etymologyText, List<Participle> inflections ) {

        List<String> senseTags = new ArrayList<>();

        // If this is a gerundive form (most participle roots with no form_of are gerundive),
        // add appropriate tags
        if(etymologyText.contains(GrammaticalParticipleTense.FUTURE_PASSIVE.getDisplayName().toLowerCase())){
            senseTags.add(GrammaticalVoice.PASSIVE.name().toLowerCase());
            senseTags.add(GrammaticalTense.FUTURE.name().toLowerCase());
            senseTags.add(GrammaticalParticipleTense.FUTURE_PASSIVE.name().toLowerCase());
            logger.trace("Adding gerundive participle tags for: {}", participleLemma);
        } else {
            logger.trace("Cannot derive participle tense from etymologyText for {}", participleLemma);
            return Optional.empty();
        }

        Conjugation.Builder conjBuilder = new Conjugation.Builder(participleLemma);

        senseTags = resolveParticipleTenseTags(senseTags);

        // resolve any conjugation attributes of participle (tense and voice)
        parserSupport.applyAllToInflection(senseTags, conjBuilder, logger);

        ParticipleDeclensionSet.Builder participleSetBuilder = new ParticipleDeclensionSet.Builder(
                conjBuilder.getVoice(),
                conjBuilder.getTense(),
                participleLemma
        );

        participleSetBuilder.addInflections(inflections);

        return participleSetBuilder.buildOptional()
                .map(participleSet -> new StagedParticipleData(
                        participleLemma,
                        participleLemma,
                        participleSet
                ));
    }

    String removeMacrons(String text) {
       return Utilities.normalizeDiacritics(text);
    }

    protected List<Participle> parseParticipleInflections(JsonNode root) {
        List<Participle> inflections = new ArrayList<>();
        JsonNode formsArray = root.path(FORMS.get());

        for (JsonNode formNode : formsArray) {
            if (!isValidParticipleForm(formNode)) {
                continue;
            }
            inflections.add(buildParticipleInflection(formNode));
        }

        return inflections;
    }

    // Build adjective inflected form
    Participle buildParticipleInflection(JsonNode formNode) {
        Participle.Builder builder = new Participle.Builder(formNode.path(FORM.get()).asString());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            parserSupport.applyToInflection(builder, tag.asString(), logger);
        }

        return builder.build();
    }

    // Replace two separate tense tags with compound.
    // Prevents single tags resolving to incorrect tenses
    List<String> resolveParticipleTenseTags(List<String> senseTags) {
        List<String> tagListRef = List.copyOf(senseTags);
        String participle = GrammaticalParticipleTense.PARTICIPLE.name().toLowerCase();

        if (tagListRef.contains(participle)){
            String present = GrammaticalTense.PRESENT.name().toLowerCase();
            String active = GrammaticalVoice.ACTIVE.name().toLowerCase();
            String future = GrammaticalTense.FUTURE.name().toLowerCase();
            String perfect = GrammaticalTense.PERFECT.name().toLowerCase();
            String passive = GrammaticalVoice.PASSIVE.name().toLowerCase();

            if( tagListRef.contains(present)){
                senseTags.add(GrammaticalParticipleTense.PRESENT_ACTIVE.name().toLowerCase());
                senseTags.add(GrammaticalVoice.ACTIVE.name().toLowerCase());
            } else if (tagListRef.contains(active) && tagListRef.contains(future)){
                senseTags.add(GrammaticalParticipleTense.FUTURE_ACTIVE.name().toLowerCase());
            } else if(tagListRef.contains(perfect) && tagListRef.contains(passive) ){
                senseTags.add(GrammaticalParticipleTense.PERFECT_PASSIVE.name().toLowerCase());
            } else if(tagListRef.contains(perfect) && tagListRef.contains(active) ){
                // For deponent participles
                senseTags.add(GrammaticalParticipleTense.PERFECT_ACTIVE.name().toLowerCase());
            } else if(tagListRef.contains(future) && !tagListRef.contains(passive) ){
                // For some deponents 'form-of' node contains a 'future' tag but no 'active' tag,
                // So creating compound from just future for those instances and adding 'active'
                // so that the 'voice' field which is part of the key is set.
                senseTags.add(GrammaticalParticipleTense.FUTURE_ACTIVE.name().toLowerCase());
                senseTags.add(GrammaticalVoice.ACTIVE.name().toLowerCase());
            } else if (tagListRef.contains(perfect)){
                senseTags.add(GrammaticalParticipleTense.PERFECT_PASSIVE.name().toLowerCase());
                senseTags.add(GrammaticalVoice.PASSIVE.name().toLowerCase());
            }
        }
        return senseTags;
    }

    boolean isValidParticipleForm(JsonNode formNode){
        return formNode.path(SOURCE.get()).asString().equals(DECLENSION.get())
                && !ParserConstants.COMMON_FORM_BLACKLIST.contains(formNode.path(FORM.get()).asString());
    }
}
