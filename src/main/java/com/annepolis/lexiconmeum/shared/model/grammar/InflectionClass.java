package com.annepolis.lexiconmeum.shared.model.grammar;

public enum InflectionClass {
    FIRST("declension-1", "conjugation-1"),
    SECOND("declension-2", "conjugation-2"),
    THIRD("declension-3", "conjugation-3"),
    FOURTH("declension-4", "conjugation-4"),
    FIFTH("declension-5", null);  // e.g., for 5th decl nouns

    private final String declensionTag;
    private final String conjugationTag;

    InflectionClass(String declensionTag, String conjugationTag) {
        this.declensionTag = declensionTag;
        this.conjugationTag = conjugationTag;
    }

    public String getDeclensionTag() { return declensionTag; }
    public String getConjugationTag() { return conjugationTag; }
}
