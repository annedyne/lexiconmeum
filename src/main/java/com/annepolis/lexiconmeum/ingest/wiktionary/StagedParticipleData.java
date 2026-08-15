package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.MorphologicalSubtype;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.util.List;

/**
 * Holds participle data that cannot yet be linked to its parent verb.
 * Used during ingestion
 */
public class StagedParticipleData implements LinkableData{

    // parent Lexeme's lemma
    private final String parentLemma;
    private final String parentLemmaWithMacrons;
    private final ParticipleDeclensionSet participleDeclensionSet;
    private final CompoundInflectionGenerator compoundInflectionGenerator;

    public StagedParticipleData(
            final String parentLemma,
            final String parentLemmaWithMacrons,
            ParticipleDeclensionSet participleDeclensionSet,
            CompoundInflectionGenerator compoundInflectionGenerator
    ){

        this.parentLemma = parentLemma;
        this.parentLemmaWithMacrons = parentLemmaWithMacrons;
        this.participleDeclensionSet = participleDeclensionSet;
        this.compoundInflectionGenerator = compoundInflectionGenerator;
    }

    public String getParentLemma() {
        return parentLemma;
    }

    public String getParentLemmaWithMacrons() {
        return parentLemmaWithMacrons;
    }

    public String getParticipleLemma() {
        return participleDeclensionSet.getTenseLemma();
    }

    public ParticipleDeclensionSet getParticipleDeclensionSet(){
        return participleDeclensionSet;
    }

    @Override
    public String toString() {
        return String.format("StagedParticiple{parent='%s', voice=%s, tense=%s, participleTense=%s, lemma='%s'}",
                parentLemma,
                participleDeclensionSet.getVoice(),
                participleDeclensionSet.getTense(),
                participleDeclensionSet.getParticipleTense(),
                participleDeclensionSet.getTenseLemma());
    }

    public String getParticipleKey() {
        return InflectionKey.buildParticipleSetKey(participleDeclensionSet.getVoice(), participleDeclensionSet.getTense());
    }

    @Override
    public String getLemma() {
       return getParticipleLemma();
    }

    @Override
    public String getLinkingLemma() {
        return getParentLemma();
    }

    @Override
    public String getLinkingLemmaWithMacrons() {
       return getParentLemmaWithMacrons();
    }

    @Override
    public Lexeme link(Lexeme lexeme) {
        LexemeBuilder builder = LexemeBuilder.fromLexeme(lexeme);
        VerbDetails.Builder verbDetailsBuilder = getOrCreateVerbDetailsBuilder(lexeme);

        verbDetailsBuilder.addParticipleSet(getParticipleDeclensionSet());

        VerbDetails verbDetails = verbDetailsBuilder.build();
        builder.setPartOfSpeechDetails(verbDetails);

        updateVerbWithGenderedCompoundForms(builder, verbDetails);

        return builder.build();
    }

    /**
     * Projects this participle set into the compound (participle + esse) forms of the
     * perfect-system tenses, one per gender. The gendered forms supersede the single
     * default form added from the main lemma entry of the lexeme
     */
    private void updateVerbWithGenderedCompoundForms(LexemeBuilder builder, VerbDetails verbDetails) {
        // if this is a perfect participle set and passive, OR it's active, but the verb is deponent,
        // go ahead and update default periphrastic form with the appropriate gendered participle.
        if (participleDeclensionSet.getTense() == GrammaticalTense.PERFECT
                && (participleDeclensionSet.getVoice() == GrammaticalVoice.PASSIVE
                || verbDetails.getMorphologicalSubtype() == MorphologicalSubtype.DEPONENT)) {

            List<Conjugation> genderedForms =
                    compoundInflectionGenerator.generateAllGenderedCompoundForms(participleDeclensionSet);

            for (Conjugation genderedForm : genderedForms) {
                builder.removeInflection(InflectionKey.buildConjugationKey(
                        genderedForm.getVoice(),
                        genderedForm.getMood(),
                        genderedForm.getTense(),
                        genderedForm.getPerson(),
                        genderedForm.getNumber()));
                builder.addInflection(genderedForm);
            }
        }
    }

    private VerbDetails.Builder getOrCreateVerbDetailsBuilder(Lexeme verb) {
        if (verb.getPartOfSpeechDetails() instanceof VerbDetails verbDetails) {

            VerbDetails.Builder vdBuilder = new VerbDetails.Builder();
            vdBuilder.setMorphologicalSubtype(verbDetails.getMorphologicalSubtype());

            verbDetails.getParticiples().values().forEach(vdBuilder::addParticipleSet);

            return vdBuilder;
        }

        // No existing details, create new
        return new VerbDetails.Builder();
    }

    @Override
    public String getDataKey() {
       return getParticipleKey();
    }

    @Override
    public PartOfSpeech getParentLinkPartOfSpeech() {
        return PartOfSpeech.VERB;
    }


}