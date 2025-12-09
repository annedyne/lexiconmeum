package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

/**
 * Holds participle data that cannot yet be linked to its parent verb.
 * Used during ingestion
 */
public class StagedParticipleData {

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
        return String.format("StagedParticiple{parent='%s', voice=%s, tense=%s, baseForm='%s'}",
                parentLemma,
                participleDeclensionSet.getVoice(),
                participleDeclensionSet.getTense(),
                participleDeclensionSet.getParticipleTense(),
                participleDeclensionSet.getTenseLemma());
    }

    public String getParticipleKey() {
        return InflectionKey.buildParticipleSetKey(participleDeclensionSet.getVoice(), participleDeclensionSet.getTense());
    }
}