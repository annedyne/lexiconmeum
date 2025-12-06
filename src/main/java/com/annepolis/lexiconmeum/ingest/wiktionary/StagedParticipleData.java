package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.util.Map;

/**
 * Holds participle data that cannot yet be linked to its parent verb.
 * Used during ingestion
 */
public class StagedParticipleData {

    // parent Lexeme's lemma
    private final String parentLemma;
    private final String parentLemmaWithMacrons;
    private final ParticipleDeclensionSet participleDeclensionSet;

    // first-person singular of participle for a given voice and tense
    private final String participleLemma;
    private final Map<String, Agreement> inflections;

    public StagedParticipleData(
            String parentLemma,
            String parentLemmaWithMacrons,
            GrammaticalVoice voice,
            GrammaticalTense tense,
            String participleLemma,
            Map<String, Agreement> inflections) {

        this.parentLemma = parentLemma;
        this.parentLemmaWithMacrons = parentLemmaWithMacrons;
        this.voice = voice;
        this.tense = tense;
        this.participleLemma = participleLemma;
        this.inflections = inflections;
    }

    public boolean isGerundive(){
        return voice == GrammaticalVoice.PASSIVE && tense == GrammaticalTense.FUTURE;
    }

    public String getParentLemma() {
        return parentLemma;
    }

    public String getParentLemmaWithMacrons() {
        return parentLemmaWithMacrons;
    }

    public String getParticipleLemma() {
        return participleLemma;
    }

    public VerbDetails.ParticipleSet toParticipleSet() {
        return new VerbDetails.ParticipleSet(voice, tense, participleLemma, inflections);
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
        return InflectionKey.buildParticipleSetKey(voice, tense);
    }

}