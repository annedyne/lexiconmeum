package com.annepolis.lexiconmeum.shared.model.grammar;

public enum InflectionClass {
    FIRST("declension-1", "conjugation-1", "1st"),
    SECOND("declension-2", "conjugation-2","2nd"),
    THIRD("declension-3", "conjugation-3","3rd"),
    FOURTH("declension-4", "conjugation-4","4th"),
    FIFTH("declension-5", null, "4th" );  // e.g."5th declension", null);  // e.g., for 5th decl nouns

    private final String declensionTag;
    private final String conjugationTag;
    private final String displayTag;

    InflectionClass(String declensionTag, String conjugationTag, String displayTag) {
        this.declensionTag = declensionTag;
        this.conjugationTag = conjugationTag;
        this.displayTag = displayTag;
    }

    public String getDeclensionTag() { return declensionTag; }
    public String getConjugationTag() { return conjugationTag; }

    public String getDisplayTag(){
        return displayTag;
    }

    public String getDisplayDeclensionTag(){
        return  displayTag + " declension";
    }

    public String getDisplayConjugationTag(){
        return  displayTag + " conjugation";
    }
}
