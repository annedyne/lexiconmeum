package com.annepolis.lexiconmeum.textsearch;

public enum WiktiionaryLexicalDataJsonKey {

    WORD("word"),
    POSITION("pos"),
    SENSES("senses"),
    GLOSSES("glosses"),
    FORMS("forms"),
    FORM("form"),
    TAGS("tags");

    private final String key;

    WiktiionaryLexicalDataJsonKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}
