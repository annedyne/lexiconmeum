package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalParticipleTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.GERUND;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.SIGMATIC;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.SUPINE;

@Component
public class POSVerbParser implements PartOfSpeechParser {
    static final Logger logger = LogManager.getLogger(POSVerbParser.class);

    private static final Set<String> TAG_BLACKLIST = Set.of(
            SIGMATIC.get()
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

    private final CompoundInflectionGenerator compoundInflectionGenerator;
    private final ParserSupport parserSupport;

    public POSVerbParser(CompoundInflectionGenerator compoundInflectionGenerator, ParserSupport parserSupport){
        this.compoundInflectionGenerator = compoundInflectionGenerator;
        this.parserSupport = parserSupport;
    }

    @Override
    public ParsedResultProcessor parsePartOfSpeech(JsonNode root, POSParserKey parserKey) {

        // Build the lexeme and return it wrapped in the appropriate processor, or EMPTY if no result
        return parserSupport.initLexemeBuilderFromRoot(root, logger)
                .flatMap(lexemeBuilder -> buildLexeme(lexemeBuilder, root))
                .map(lexeme -> (ParsedResultProcessor) (
                        lexemeConsumer,
                        stagingService) -> stagingService.stageLexeme(lexeme)
                )
                .orElse(ParsedResultProcessor.EMPTY);
    }

    private Optional<Lexeme> buildLexeme(LexemeBuilder lexemeBuilder, JsonNode root){
        parserSupport.addSenses(root.path(SENSES.get()), lexemeBuilder, logger);

        JsonNode formsNode = root.path(FORMS.get());
        addInflections( lexemeBuilder, formsNode);

        return new SafeBuilder<>(PartOfSpeech.VERB.name(), lexemeBuilder::build).build(logger, parserSupport.getParseMode());

    }

    public void addInflections(LexemeBuilder lexemeBuilder, JsonNode formsNode) {
        Map<GrammaticalTense, List<Participle>> verbalNounInflections = new EnumMap<>(GrammaticalTense.class);
        verbalNounInflections.put(GrammaticalTense.SUPINE, new ArrayList<>());

        for (JsonNode formNode : formsNode) {
            try {
                if (parserSupport.isValidFormNode(formNode, PartOfSpeech.VERB.getInflectionTypeLower())) {
                   
                    String formValue = formNode.path(FORM.get()).asString();
                    List<String> tags = collectTags(formNode);

                    if (isVerbalNoun(tags)) {
                        GrammaticalTense verbalNounTense = getVerbalNounTense(tags);
                        verbalNounInflections.computeIfAbsent(verbalNounTense, tense -> new ArrayList<>())
                                .add(buildVerbalNounInflection(formValue, tags));
                        continue;
                    }

                    Optional<Conjugation> optionalConjugation = buildConjugation(formValue, tags);
                    addInflection(lexemeBuilder, optionalConjugation);
                } else {
                    // Canonical just tags a form already in the form array.
                    // so no need to add it again as an inflection
                    parserSupport.addCanonicalForm( formNode, lexemeBuilder);
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                logger.trace(ParserSupport.LogMsg.SKIPPING_INVALID_FORM, ex.getMessage());
            }
        }

        addVerbalNounDeclensionSets(lexemeBuilder, verbalNounInflections);
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
        // The main-verb data carries only the masculine participle base, so this
        // generates the ungendered baseline (gender null). Feminine/neuter variants
        // are added post-link once the participle declension sets are available.
        compoundInflectionGenerator.generateCompoundForms(
                        conjugation.getForm(),
                        conjugation.getVoice(),
                        conjugation.getMood(),
                        conjugation.getTense(),
                        null)
                .forEach(lexemeBuilder::addInflection);
    }


    Optional<Conjugation> buildConjugation(String formValue, List<String> tags){
        if (hasBlacklistedTag(tags)) {
            return Optional.empty();
        }
        formValue = normalizeFormValue(formValue);
        Conjugation.Builder builder = new Conjugation.Builder(formValue);

        coalesceCompoundFutureTenseTags(tags);

        for (String tag : tags){
            parserSupport.applyToInflection(builder, tag, logger);
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
            tags.add(tag.asString().toLowerCase());
        }
        return tags;
    }

    // Replace two separate tense tags with compound
    private void coalesceCompoundFutureTenseTags(List<String> tags) {
        String future = GrammaticalTense.FUTURE.name().toLowerCase();
        String perfect = GrammaticalTense.PERFECT.name().toLowerCase();
        String participle = GrammaticalParticipleTense.PARTICIPLE.name().toLowerCase();

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
                tags.add(GrammaticalParticipleTense.PRESENT_ACTIVE.name().toLowerCase());
            }

            if(tags.contains(GrammaticalTense.PERFECT.name().toLowerCase())){
                tags.remove(participle);
                tags.remove(present);
                tags.add(GrammaticalParticipleTense.PRESENT_ACTIVE.name().toLowerCase());
            }
        }
    }


    // --------------------------------------- VERBAL NOUN METHODS ---------------------------------- //

    private boolean isVerbalNoun(List<String> tags) {
        return tags.contains(SUPINE.get()) || tags.contains(GERUND.get());
    }

    private GrammaticalTense getVerbalNounTense(List<String> tags) {
        return tags.contains(SUPINE.get()) ? GrammaticalTense.SUPINE : GrammaticalTense.GERUND;
    }

    private Participle buildVerbalNounInflection(String formValue, List<String> tags){
        // Verbal nouns apply to all genders but Wiktionary doesn't add gender tags to their forms.
        for(GrammaticalGender gender : GrammaticalGender.values()){
            tags.add(gender.getTag());
        }

        return buildParticiple(formValue, tags);
    }

    private Participle buildParticiple(String formValue, List<String> tags) {
        Participle.Builder builder = new Participle.Builder(normalizeFormValue(formValue));
        for (String tag : tags) {
            parserSupport.applyToInflection(builder, tag, logger);
        }
        return builder.build();
    }

    private void addVerbalNounDeclensionSets(
            LexemeBuilder lexemeBuilder,
            Map<GrammaticalTense, List<Participle>> verbalNounInflections
    ) {
        VerbDetails.Builder verbDetailsBuilder = getVerbDetailsBuilder(lexemeBuilder);
        for (Map.Entry<GrammaticalTense, List<Participle>> entry : verbalNounInflections.entrySet()) {
            ParticipleDeclensionSet verbalNounSet = ParticipleDeclensionSet.Builder
                    .forVerbalNoun(entry.getKey(), lexemeBuilder.getLemma())
                    .addInflections(entry.getValue())
                    .build();
            verbDetailsBuilder.addParticipleSet(verbalNounSet);
        }

        lexemeBuilder.setPartOfSpeechDetails(verbDetailsBuilder.build());
    }

    private VerbDetails.Builder getVerbDetailsBuilder(LexemeBuilder lexemeBuilder) {
        if (lexemeBuilder.getPartOfSpeechDetails() instanceof VerbDetails details) {
            return details.toBuilder();
        }
        return new VerbDetails.Builder();
    }
}
