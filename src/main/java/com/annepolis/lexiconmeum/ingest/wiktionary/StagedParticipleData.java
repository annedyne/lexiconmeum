package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

/**
 * Holds participle data that cannot yet be linked to its parent verb.
 * Used during ingestion
 */
public class StagedParticipleData implements LinkableData{

    // parent Lexeme's lemma
    private final String parentLemma;
    private final String parentLemmaWithMacrons;
    private final ParticipleDeclensionSet participleDeclensionSet;

    public StagedParticipleData(
            final String parentLemma,
            final String parentLemmaWithMacrons,
            ParticipleDeclensionSet participleDeclensionSet
    ){

        this.parentLemma = parentLemma;
        this.parentLemmaWithMacrons = parentLemmaWithMacrons;
        this.participleDeclensionSet = participleDeclensionSet;
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

        builder.setPartOfSpeechDetails(verbDetailsBuilder.build());

        return builder.build();
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
}