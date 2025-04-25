package com.annepolis.lexiconmeum.util;

public enum JsonKey {

    WORD("word"),
    POSITION("pos"),
    SENSES("senses"),
    GLOSSES("glosses"),
    FORMS("forms"),
    FORM("form"),
    TAGS("tags");

    private final String key;

    JsonKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}
