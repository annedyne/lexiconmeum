package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.util.*;

public final class VerbDetails implements PartOfSpeechDetails {

    private final MorphologicalSubtype morphologicalSubtype;
    private final Map<String, ParticipleSet> participles;

    public VerbDetails(Builder builder) {
        this.morphologicalSubtype = builder.morphologicalSubtype;
        this.participles = Collections.unmodifiableMap(new HashMap<>(builder.participles));
    }

    public MorphologicalSubtype getMorphologicalSubtype() {
        return morphologicalSubtype;
    }

    public Map<String, ParticipleSet> getParticiples() {
        return participles;
    }

    /**
     * Get a specific participle set by voice and tense
     */
    public Optional<ParticipleSet> getParticipleSet(GrammaticalVoice voice, GrammaticalTense tense) {
        String key = InflectionKey.buildParticipleSetKey(voice, tense);
        return Optional.ofNullable(participles.get(key));
    }

    /**
     * Check if verb has any participles
     */
    public boolean hasParticiples() {
        return !participles.isEmpty();
    }


    public VerbDetails.Builder toBuilder() {
        VerbDetails.Builder builder = new VerbDetails.Builder().setMorphologicalSubtype(morphologicalSubtype);
        getParticiples().values().forEach(ps -> builder.addParticipleSet(ps));
        return builder;
    }
    /**
     * Builder for constructing VerbDetails incrementally
     */
    public static class Builder {
        private MorphologicalSubtype morphologicalSubtype;
        private final Map<String, ParticipleSet> participles = new HashMap<>();

        public Builder() {
        }

        public static VerbDetails.Builder fromVerbDetails(VerbDetails details) {

            VerbDetails.Builder builder = new VerbDetails.Builder();
            builder.setMorphologicalSubtype(details.getMorphologicalSubtype());

            details.getParticiples().values().forEach(ps -> builder.addParticipleSet(ps));
            return builder;
        }

        public Builder setMorphologicalSubtype(MorphologicalSubtype morphologicalSubtype) {
            this.morphologicalSubtype = morphologicalSubtype;
            return this;
        }

        /**
         * Add a complete participle set
         */
        public Builder addParticipleSet(ParticipleSet participleSet) {
            this.participles.put(participleSet.getParticipleKey(), participleSet);
            return this;
        }

        public VerbDetails build() {
            return new VerbDetails(this);
        }
    }

    /**
     * Container for all inflected forms of a single participle
     */
    public static final class ParticipleSet {
        private final GrammaticalVoice voice;
        private final GrammaticalTense tense;
        private final String baseForm;
        private final Map<String, Agreement> inflections;

        public ParticipleSet(GrammaticalVoice voice,
                             GrammaticalTense tense,
                             String baseForm,
                             Map<String, Agreement> inflections) {
            this.voice = voice;
            this.tense = tense;
            this.baseForm = baseForm;
            this.inflections = Collections.unmodifiableMap(new HashMap<>(inflections));
        }

        public GrammaticalVoice getVoice() {
            return voice;
        }

        public GrammaticalTense getTense() {
            return tense;
        }

        public String getBaseForm() {
            return baseForm;
        }

        public Map<String, Agreement> getInflections() {
            return inflections;
        }

        public String getParticipleKey() {
            return InflectionKey.buildParticipleSetKey(voice, tense);
        }

        /**
         * Get a specific declined form
         */
        public Optional<Agreement> getInflection(GrammaticalCase grammaticalCase,
                                                  GrammaticalNumber number,
                                                  Set<GrammaticalGender> genders,
                                                  GrammaticalDegree degree) {
            String key = InflectionKey.joinAgreementParts(grammaticalCase, number, genders, degree);
            return Optional.ofNullable(inflections.get(key));
        }

        /**
         * Get all inflections for a specific case
         */
        public List<Agreement> getInflectionsForCase(GrammaticalCase grammaticalCase) {
            return inflections.values().stream()
                    .filter(p -> p.getGrammaticalCase() == grammaticalCase)
                    .toList();
        }
    }
}