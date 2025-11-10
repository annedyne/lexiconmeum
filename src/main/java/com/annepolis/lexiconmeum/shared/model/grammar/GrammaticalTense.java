package com.annepolis.lexiconmeum.shared.model.grammar;

// Order is important (affects api response)
public enum GrammaticalTense {

    PRESENT("Present","Simple Present"),
    IMPERFECT("Imperfect","Past Progressive"),
    PERFECT("perfect","Present Perfect"),
    PLUPERFECT("Pluperfect","Past Perfect"),
    FUTURE("Future", "Simple Future"),
    FUTURE_PERFECT("Future Perfect","Future Perfect");


    private final String historicalName;
    private final String alternativeName;


    GrammaticalTense(String historicalName, String alternativeName) {
        this.alternativeName = alternativeName;
        this.historicalName = historicalName;
    }

    public String getHistoricalName() {
        return historicalName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }
}
