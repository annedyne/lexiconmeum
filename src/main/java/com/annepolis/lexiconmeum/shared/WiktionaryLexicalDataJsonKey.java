package com.annepolis.lexiconmeum.shared;

public enum WiktionaryLexicalDataJsonKey {

    WORD("word"),
    POSITION("pos"),
    NOUN("noun"),
    VERB("verb"),
    DECLENSION("declension"),
    CONJUGATION("conjugation"),
    SOURCE("source"),
    SENSES("senses"),
    GLOSSES("glosses"),
    FORMS("forms"),
    FORM("form"),
    TAGS("tags");

    private final String key;

    WiktionaryLexicalDataJsonKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}
