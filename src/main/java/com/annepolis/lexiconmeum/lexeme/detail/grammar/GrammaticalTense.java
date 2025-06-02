package com.annepolis.lexiconmeum.lexeme.detail.grammar;

public enum GrammaticalTense {

    PRESENT("Present","Simple Present"),
    IMPERFECT("Imperfect","Past Progressive"),
    PERFECT("perfect","Simple Present"),
    PLUPERFECT("Pluperfect","Past Perfect"),
    FUTURE("Future", "Simple Future"),
    FUTURE_PERFECT("Future Perfect","Future Perfect");

    private final String historicalName;
    private final String alternativeName;


    GrammaticalTense(String historicalName, String alternativeName) {
        this.alternativeName = alternativeName;
        this.historicalName = historicalName;
    }
}
