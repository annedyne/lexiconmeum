package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalParticipleTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.FORM_OF;

@Component
public class POSParticipleParser {

    LexicalTagResolver lexicalTagResolver;


    public POSParticipleParser(LexicalTagResolver lexicalTagResolver){
        this.lexicalTagResolver = lexicalTagResolver;
    }

    static final Logger logger = LogManager.getLogger(POSParticipleParser.class);

    /**
     * Check if this verb entry is actually a participle form entry
     */
    protected boolean isValidParticipleEntry(JsonNode root) {
        // Must have head_templates
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        if (!headTemplates.isArray() || headTemplates.isEmpty()) {
            return false;
        }

        // Check template name
        String templateName = headTemplates.get(0).path(NAME.get()).asText("");
        return WiktionaryLexicalDataKeyWord.TEMPLATE_HEAD_PARTICPLE.get().equals(templateName);
    }
    Optional<ParticipleDeclensionSet> parseParticiple(VerbDetails.Builder builder, JsonNode jsonNode){

        return Optional.empty();
    }

    /**
     * Parse a participle entry and return staged data.
     * This is called when processing a participle JSONL entry (not verb inflection data).
     */
    public StagedParticipleData parseParticipleEntry(JsonNode root) {
        String participleLemma = root.path(WORD.get()).asText();

        // INITIALIZE WITH GERUNDIVE FORM
        String parentLemmaWithMacrons = participleLemma;
        String parentLemma = participleLemma;
        Conjugation.Builder conjBuilder = new Conjugation.Builder(parentLemmaWithMacrons);
        List<String> senseTags = new ArrayList<>();

        // add tense tags appropriate for gerundive
        // these get overwritten if not gerundive (if no 'form-of' tag)
        senseTags.add(GrammaticalVoice.PASSIVE.name().toLowerCase());
        senseTags.add(GrammaticalTense.FUTURE.name().toLowerCase());
        senseTags.add(GrammaticalParticipleTense.FUTURE_PASSIVE.name().toLowerCase());

        // Extract parent verb information from form_of if it exists,
        // and overwrite parent lemma
        // NB: Assuming the same tags for all senses for now
        JsonNode formOfArray = root.path(SENSES.get()).get(0).path(FORM_OF.get());
        if (formOfArray != null && !formOfArray.isEmpty()) {
            parentLemmaWithMacrons = formOfArray.get(0).path(WORD.get()).asText();
            parentLemma = removeMacrons(parentLemmaWithMacrons);

            JsonNode senseNode = root.path(SENSES.get()).get(0);
            senseTags = collectTags(senseNode);
            resolveParticipleCompoundTenseTags(senseTags);
        }

        // resolve any conjugation attributes of participle (tense and voice)
        lexicalTagResolver.applyAllToInflection(senseTags, conjBuilder, logger);

        // Build the agreement/declension attributes - case inflections.
        List<Participle> inflections = parseParticipleInflections(root);

        ParticipleDeclensionSet.Builder participleSetBuilder = new ParticipleDeclensionSet.Builder(
                conjBuilder.getVoice(),
                conjBuilder.getTense(),
                participleLemma
                );
        participleSetBuilder.addInflections(inflections);

        return new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                participleSetBuilder.build()
        );
    }

    String removeMacrons(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
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
        Participle.Builder builder = new Participle.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            lexicalTagResolver.applyToInflection(builder, tag.asText(), logger);
        }

        return builder.build();
    }

    // Replace two separate tense tags with compound.
    // Prevents single tags resolving to incorrect tenses
    private List<String> resolveParticipleCompoundTenseTags(List<String> senseTags) {
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
            } else if (tagListRef.contains(active) && tagListRef.contains(future)){
                senseTags.add(GrammaticalParticipleTense.FUTURE_ACTIVE.name().toLowerCase());
            } else if(tagListRef.contains(perfect) && tagListRef.contains(passive) ){
                senseTags.add(GrammaticalParticipleTense.PERFECT_PASSIVE.name().toLowerCase());
            }
        }
        return senseTags;
    }

    boolean isValidParticipleForm(JsonNode formNode){
        return formNode.path(SOURCE.get()).asText().equals(DECLENSION.get())
                && !ParserConstants.COMMON_FORM_BLACKLIST.contains(formNode.path(FORM.get()).asText());
    }

    private List<String> collectTags(JsonNode tagParentNode) {
        List<String> tags = new ArrayList<>();
        for (JsonNode tag : tagParentNode.path(TAGS.get())) {
            tags.add(tag.asText().toLowerCase());
        }
        return tags;
    }
}
