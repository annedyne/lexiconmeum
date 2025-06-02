package com.annepolis.lexiconmeum.lexeme.detail.grammar;

public enum GrammaticalMood {
    INDICATIVE("Indicative","Indicative"),
    SUBJUNCTIVE("Subjunctive","Progressive"),;

    private final String historicalName;
    private final String alternativeName;

    GrammaticalMood(String historicalName, String alternativeName){
        this.historicalName = historicalName;
        this.alternativeName = alternativeName;
    }
}
