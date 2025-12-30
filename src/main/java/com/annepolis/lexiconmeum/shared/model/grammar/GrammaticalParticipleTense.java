package com.annepolis.lexiconmeum.shared.model.grammar;

public enum GrammaticalParticipleTense {
    PARTICIPLE("Participle", "Participle"),
    PRESENT_ACTIVE("Present Active Participle", "Present Active Participle"),
    PERFECT_PASSIVE("Perfect Passive Participle", "Perfect Passive Participle"),
    FUTURE_ACTIVE("Future Active Participle", "Future Active Participle"),
    FUTURE_PASSIVE("Gerundive", "Future Passive Participle");

    private final String displayName;
    private final String alternativeName;

    GrammaticalParticipleTense(String displayName, String alternativeName) {

        this.displayName = displayName;
        this.alternativeName = alternativeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    /**
     * Derives the participle tense from the given voice and tense combination.
     *
     * @param voice the grammatical voice
     * @param tense the grammatical tense
     * @return the corresponding participle tense
     * @throws IllegalArgumentException if the combination is not valid for participles
     */
    public static GrammaticalParticipleTense fromVoiceAndTense(GrammaticalVoice voice, GrammaticalTense tense) {
        if (voice == GrammaticalVoice.ACTIVE && tense == GrammaticalTense.PRESENT) {
            return PRESENT_ACTIVE;
        } else if (voice == GrammaticalVoice.ACTIVE && tense == GrammaticalTense.FUTURE) {
            return FUTURE_ACTIVE;
        } else if (voice == GrammaticalVoice.PASSIVE && tense == GrammaticalTense.PERFECT) {
            return PERFECT_PASSIVE;
        } else if (voice == GrammaticalVoice.PASSIVE && tense == GrammaticalTense.FUTURE) {
            return FUTURE_PASSIVE;
        } else {
            throw new IllegalArgumentException(
                    "Invalid participle combination: " + voice + " " + tense
            );
        }
    }
}
