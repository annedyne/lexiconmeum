package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.util.Map;

/**
 * Holds participle data that cannot yet be linked to its parent verb.
 * Used during ingestion when participles are encountered before their verbs.
 */
public class StagedParticipleData {
    private final String parentLemma;  // without macrons, e.g., "amo"
    private final String parentLemmaWithMacrons;  // with macrons from form_of, e.g., "amō"
    private final GrammaticalVoice voice;
    private final GrammaticalTense tense;
    private final String baseForm;  // e.g., "amans"
    private final Map<String, Agreement> inflections;

    public StagedParticipleData(
            String parentLemma,
            String parentLemmaWithMacrons,
            GrammaticalVoice voice,
            GrammaticalTense tense,
            String baseForm,
            Map<String, Agreement> inflections) {
        this.parentLemma = parentLemma;
        this.parentLemmaWithMacrons = parentLemmaWithMacrons;
        this.voice = voice;
        this.tense = tense;
        this.baseForm = baseForm;
        this.inflections = inflections;
    }

    public String getParentLemma() {
        return parentLemma;
    }

    public String getParentLemmaWithMacrons() {
        return parentLemmaWithMacrons;
    }

    public String getBaseForm() {
        return baseForm;
    }

    public VerbDetails.ParticipleSet toParticipleSet() {
        return new VerbDetails.ParticipleSet(voice, tense, baseForm, inflections);
    }

    @Override
    public String toString() {
        return String.format("StagedParticiple{parent='%s', voice=%s, tense=%s, baseForm='%s'}",
                parentLemma, voice, tense, baseForm);
    }

    public String getParticipleKey() {
        return InflectionKey.buildParticipleSetKey(voice, tense);
    }

}