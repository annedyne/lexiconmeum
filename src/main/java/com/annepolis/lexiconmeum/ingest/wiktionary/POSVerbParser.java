package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.TEMPLATE_HEAD_VERB;

@Component
public class POSVerbParser implements PartOfSpeechParser {
    static final Logger logger = LogManager.getLogger(POSVerbParser.class);

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

    private static final Set<String> COMPOUND_TENSE_KEY_LIST = Set.of(
            "ACTIVE|INDICATIVE|PERFECT",
            "ACTIVE|INDICATIVE|PLUPERFECT",
            "ACTIVE|INDICATIVE|FUTURE_PERFECT",
            "ACTIVE|SUBJUNCTIVE|PERFECT",
            "ACTIVE|SUBJUNCTIVE|PLUPERFECT",
            "ACTIVE|SUBJUNCTIVE|FUTURE_PERFECT",
            "PASSIVE|INDICATIVE|PERFECT",
            "PASSIVE|INDICATIVE|PLUPERFECT",
            "PASSIVE|INDICATIVE|FUTURE_PERFECT",
            "PASSIVE|SUBJUNCTIVE|PERFECT",
            "PASSIVE|SUBJUNCTIVE|PLUPERFECT",
            "PASSIVE|SUBJUNCTIVE|FUTURE_PERFECT"
    );

    public static final Set<String> VALID_HEAD_TEMPLATE_NAMES = Set.of(TEMPLATE_HEAD_VERB.get());
    private final LexicalTagResolver lexicalTagResolver;
    private final EsseFormProvider esseFormProvider;

    public POSVerbParser(LexicalTagResolver lexicalTagResolver, EsseFormProvider esseFormProvider){
        this.lexicalTagResolver = lexicalTagResolver;
        this.esseFormProvider = esseFormProvider;
    }

    @Override
    public boolean validate(JsonNode root) {

         // Only process full verb structures, not separate form structures
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        String templateName = headTemplates.get(0).path(NAME.get()).asText("");

        return VALID_HEAD_TEMPLATE_NAMES.contains(templateName);

    }

    // Filter out form nodes in the blacklist
    private boolean isConjugationForm(JsonNode formNode){
        String formValue = formNode.path(FORM.get()).asText();

        return CONJUGATION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                && !FORM_BLACKLIST.contains(formValue);
    }

    @Override
    public Optional<Lexeme> parsePartOfSpeech(LexemeBuilder lexemeBuilder, JsonNode root){
        JsonNode formsNode = root.path(FORMS.get());
        addInflections( lexemeBuilder, formsNode);
        try {
            // Return the built lexeme instead of staging it directly
            return Optional.of(lexemeBuilder.build());
        } catch (Exception ex) {
            logger.warn(WiktionaryLexicalDataParser.LogMsg.FAILED_TO_BUILD, ex.getMessage());
            return Optional.empty();
        }

    }

    @Override
    public void addInflections(LexemeBuilder lexemeBuilder, JsonNode formsNode) {
        for (JsonNode formNode : formsNode) {
            try {
                if (isConjugationForm(formNode)) {
                   
                    String formValue = formNode.path(FORM.get()).asText();
                    List<String> tags = collectTags(formNode);

                    Optional<Conjugation> optionalConjugation = buildConjugation(formValue, tags);
                    addInflection(lexemeBuilder, optionalConjugation);
                } else {
                    // Canonical just tags a form already in the form array.
                    // so no need to add it again as an inflection
                    addCanonicalForm(lexemeBuilder, formNode);
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                logger.trace(WiktionaryLexicalDataParser.LogMsg.SKIPPING_INVALID_FORM, ex.getMessage());
            }
        }
    }


    private void addInflection(LexemeBuilder lexemeBuilder, Optional<Conjugation> optionalConjugation){
        if(optionalConjugation.isPresent()) {
            // Handle compound forms
            Conjugation conjugation = optionalConjugation.get();
            String key = InflectionKey.of(conjugation);
            if (COMPOUND_TENSE_KEY_LIST.contains(key)) {
                addCompoundInflectionForms(lexemeBuilder, conjugation);
            } else {
                lexemeBuilder.addInflection(conjugation);
            }
        }
    }

    /**
     * Adds compound inflection forms to the given {@code LexemeBuilder} based on
     * the provided {@code Conjugation}. It generates compound forms by combining
     * a participle base with corresponding forms of the verb "to be" (esse).
     *
     * @param lexemeBuilder the builder for constructing a lexeme, where the
     *                      generated compound inflection forms will be added
     * @param conjugation   the conjugation from which the participle base and
     *                      additional grammatical properties (such as mood, tense, voice,
     *                      number, and person) are derived to construct compound forms
     */
    private void addCompoundInflectionForms(LexemeBuilder lexemeBuilder, Conjugation conjugation) {

        String participleBase = conjugation.getForm();
        // NB: Even if this inflection has previously been built with all forms.
        // We still need to fetch it from the Lexeme to set the current form as the alternative.
        for (GrammaticalNumber number : GrammaticalNumber.values()) {
            for (GrammaticalPerson person : GrammaticalPerson.values()) {
                String esseForm = esseFormProvider.getForm(conjugation.getMood(), conjugation.getTense(), number, person);

                String compoundForm = participleBase + " " + esseForm;

                Conjugation compoundConjugation = new Conjugation.Builder(compoundForm)
                        .setVoice(conjugation.getVoice())
                        .setMood(conjugation.getMood())
                        .setTense(conjugation.getTense())
                        .setNumber(number)
                        .setPerson(person)
                        .build();

                lexemeBuilder.addInflection(compoundConjugation);
            }
        }
    }


    // If this form contains a 'canonical' tag, add it to Lexeme canonical forms 
    private void addCanonicalForm(LexemeBuilder lexemeBuilder, JsonNode formNode) throws IllegalArgumentException{
        for (JsonNode tag : formNode.path(TAGS.get())) {
            if(CANONICAL.name().equalsIgnoreCase(tag.asText())){
                lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asText());
                break;
            }
        }
    }


    Optional<Conjugation> buildConjugation(String formValue, List<String> tags){
        if (hasBlacklistedTag(tags)) {
            return Optional.empty();
        }
        formValue = normalizeFormValue(formValue);
        Conjugation.Builder builder = new Conjugation.Builder(formValue);

        coalesceCompoundFutureTenseTags(tags);

        for (String tag : tags){
            lexicalTagResolver.applyToInflection(builder, tag, logger);
        }

        return Optional.of(builder.build());
    }

    private String normalizeFormValue(String formValue){
        String normalized = StringUtils.substringBefore(formValue, "+");
        return StringUtils.normalizeSpace(normalized);
    }

    private boolean hasBlacklistedTag(List<String> tags) {
        for (String tag : tags) {
            if (TAG_BLACKLIST.contains(tag)) {
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
    private void coalesceCompoundFutureTenseTags(List<String> tags) {
        String future = GrammaticalTense.FUTURE.name().toLowerCase();
        String perfect = GrammaticalTense.PERFECT.name().toLowerCase();
        String participle = GrammaticalParticiple.PARTICIPLE.name().toLowerCase();

        if (tags.contains(future) && tags.contains(perfect)) {
            tags.remove(future);
            tags.remove(perfect);
            tags.add(GrammaticalTense.FUTURE_PERFECT.name().toLowerCase());
        }
        if (tags.contains(participle) ){
            String present = GrammaticalTense.PRESENT.name().toLowerCase();
            String active = GrammaticalVoice.ACTIVE.name().toLowerCase();
            if(tags.contains(active) && tags.contains(present)){
                tags.remove(participle);
                tags.remove(present);
                tags.remove(active);
                tags.add(GrammaticalParticiple.PRESENT_ACTIVE.name().toLowerCase());
            }

            if(tags.contains(GrammaticalTense.PERFECT.name().toLowerCase())){
                tags.remove(participle);
                tags.remove(present);
                tags.add(GrammaticalParticiple.PRESENT_ACTIVE.name().toLowerCase());
            }
        }
    }
}
