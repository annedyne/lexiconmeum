package com.annepolis.lexiconmeum.shared.model.grammar;

public enum GrammaticalMood {
    INDICATIVE("Indicative","Indicative"),
    SUBJUNCTIVE("Subjunctive","Progressive"),
    INFINITIVE("Infinitive","Infinitive"),
    IMPERATIVE("Imperative","Imperative");

    private final String historicalName;
    private final String alternativeName;

    GrammaticalMood(String historicalName, String alternativeName){
        this.historicalName = historicalName;
        this.alternativeName = alternativeName;
    }

    public String getHistoricalName() {
        return historicalName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }
}
