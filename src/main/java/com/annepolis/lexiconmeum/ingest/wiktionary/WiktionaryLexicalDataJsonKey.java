package com.annepolis.lexiconmeum.ingest.wiktionary;

public enum WiktionaryLexicalDataJsonKey {

    WORD("word"),
    PART_OF_SPEECH("pos"),
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
    CANONICAL("canonical"),
    ETYMOLOGY_NUMBER("etymology_number");

    private final String key;

    WiktionaryLexicalDataJsonKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}
