package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class VerbDetails implements PartOfSpeechDetails {

    private final MorphologicalSubtype morphologicalSubtype;
    private final Map<String, ParticipleDeclensionSet> participleDeclensionSets;

    public VerbDetails(Builder builder) {
        this.morphologicalSubtype = builder.morphologicalSubtype;
        this.participleDeclensionSets = Collections.unmodifiableMap(new TreeMap<>(builder.participleDeclensionSets));
    }

    public MorphologicalSubtype getMorphologicalSubtype() {
        return morphologicalSubtype;
    }
    public Map<String, ParticipleDeclensionSet> getParticiples() {
        return participleDeclensionSets;
    }


    public Optional<ParticipleDeclensionSet> getParticipleSet(GrammaticalVoice voice, GrammaticalTense tense) {
        String key = InflectionKey.buildParticipleSetKey(voice, tense);
        return Optional.ofNullable(participleDeclensionSets.get(key));
    }

    public VerbDetails.Builder toBuilder() {
        VerbDetails.Builder builder = new VerbDetails.Builder().setMorphologicalSubtype(morphologicalSubtype);
        getParticiples().values().forEach(builder::addParticipleSet);
        return builder;
    }

    public static class Builder {
        private MorphologicalSubtype morphologicalSubtype;
        private final Map<String, ParticipleDeclensionSet> participleDeclensionSets = new TreeMap<>();
        
        public Builder setMorphologicalSubtype(MorphologicalSubtype morphologicalSubtype) {
            this.morphologicalSubtype = morphologicalSubtype;
            return this;
        }


        public Builder addParticipleSet(ParticipleDeclensionSet participleSet) {
            this.participleDeclensionSets.put(participleSet.getParticipleSetKey(), participleSet);
            return this;
        }

        public VerbDetails build() {
            return new VerbDetails(this);
        }
    }

   
}