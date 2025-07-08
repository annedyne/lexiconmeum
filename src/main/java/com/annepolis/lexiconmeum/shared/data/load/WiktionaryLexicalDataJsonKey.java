package com.annepolis.lexiconmeum.shared.data.load;

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
    TAGS("tags"),
    CANONICAL("canonical");

    private final String key;

    WiktionaryLexicalDataJsonKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}
