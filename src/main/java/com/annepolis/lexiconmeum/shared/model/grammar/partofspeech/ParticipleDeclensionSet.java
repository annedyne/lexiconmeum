package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalParticipleTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Represents a set of participle forms associated with a specific grammatical voice, tense,
 * and participle tense. Provides functionality for managing and indexing inflection forms
 * within the set.
 */
public class ParticipleDeclensionSet {
    private final GrammaticalVoice voice;
    private final GrammaticalTense tense;
    private final GrammaticalParticipleTense participleTense;
    private final String tenseLemma;
    private final Map<String, Inflection> inflectionIndex;

    ParticipleDeclensionSet(Builder builder) {
        this.voice = builder.voice;
        this.tense = builder.tense;
        this.participleTense = builder.participleTense;
        this.tenseLemma = builder.tenseLemma;
        this.inflectionIndex = builder.inflectionIndex;
    }

    public String getParticipleSetKey(){
        return InflectionKey.buildParticipleSetKey(voice, tense);
    }

    public Map<String, Inflection> getInflectionIndex() {
        return inflectionIndex;
    }

    public GrammaticalVoice getVoice() {
        return voice;
    }

    public GrammaticalTense getTense() {
        return tense;
    }

    public GrammaticalParticipleTense getParticipleTense() {
        return participleTense;
    }

    public String getTenseLemma() {
        return tenseLemma;
    }

    public static class Builder {

        private final GrammaticalVoice voice;
        private final GrammaticalTense tense;
        private final GrammaticalParticipleTense participleTense;
        private final String tenseLemma;
        private final Map<String, Inflection> inflectionIndex = new HashMap<>();

        public Builder(
                @NonNull GrammaticalVoice voice,
                @NonNull GrammaticalTense tense,
                @NonNull String tenseLemma
        ){
            this.voice = voice;
            this.tense = tense;
            this.tenseLemma = tenseLemma;
            this.participleTense = GrammaticalParticipleTense.fromVoiceAndTense(voice, tense);
        }

        public ParticipleDeclensionSet build() {
            return new ParticipleDeclensionSet(this);
        }

        public ParticipleDeclensionSet.Builder addInflections(List<Participle> inflections) {
            for (Inflection inflection : inflections) {
                addInflection(inflection);
            }
            return this;
        }

        public ParticipleDeclensionSet.Builder addInflection(Inflection inflection) {
            // if an existing inflection (for the same key) already exists,
            // set current as the alternativeForm
            addInflection(inflection, existing ->
                    existing.toBuilder()
                            .setAlternativeForm(inflection.getForm())
                            .build()
            );
            return this;
        }

        /**
         * Adds an inflection to the inflection index. If an inflection with the same key
         * already exists, merges the existing inflection with the provided one using
         * the specified mergeFunction. Otherwise, inserts the new inflection directly.
         *
         * @param inflection the inflection to be added or merged into the index
         * @param mergeFunction a function that defines how two inflections with the same
         *                      key should be merged
         */
        public ParticipleDeclensionSet.Builder addInflection(Inflection inflection, UnaryOperator<Inflection> mergeFunction) {
            String key = InflectionKey.of(inflection);
            if (inflectionIndex.containsKey(key)) {
                Inflection existing = inflectionIndex.get(key);
                Inflection merged = mergeFunction.apply(existing);
                inflectionIndex.put(key, merged);
            } else {
                inflectionIndex.put(key, inflection);
            }
            return this;
        }

        public GrammaticalVoice getVoice() {
            return voice;
        }

        public GrammaticalTense getTense() {
            return tense;
        }

        public GrammaticalParticipleTense getParticipleTense() {
            return participleTense;
        }

        public String getTenseLemma() {
            return tenseLemma;
        }

        public Map<String, Inflection> getInflections() {
            return inflectionIndex;
        }
    }

}
