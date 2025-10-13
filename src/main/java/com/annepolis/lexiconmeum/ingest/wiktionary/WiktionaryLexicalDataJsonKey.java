package com.annepolis.lexiconmeum.ingest.wiktionary;

public enum WiktionaryLexicalDataJsonKey {

    CANONICAL("canonical"),
    CONJUGATION("conjugation"),
    DECLENSION("declension"),
    ETYMOLOGY_NUMBER("etymology_number"),
    FORM("form"),
    FORMS("forms"),
    GLOSSES("glosses"),
    HEAD_TEMPLATES("head_templates"),
    NOUN("noun"),
    PART_OF_SPEECH("pos"),
    SENSES("senses"),
    SOURCE("source"),
    TAGS("tags"),
    VERB("verb"),
    WORD("word");

    private final String key;

    WiktionaryLexicalDataJsonKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}
