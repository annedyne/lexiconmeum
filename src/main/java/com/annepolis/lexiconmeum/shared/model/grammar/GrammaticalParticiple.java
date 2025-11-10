package com.annepolis.lexiconmeum.shared.model.grammar;

public enum GrammaticalParticiple {
    PARTICIPLE("Participle"),
    PRESENT_ACTIVE("Present Active Participle"),
    PERFECT_PASSIVE("Perfect Passive Participle"),
    FUTURE_ACTIVE("Future Active Participle"),
    FUTURE_PASSIVE("Future Passive Participle"); // gerundive

    private final String displayName;

    GrammaticalParticiple(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
