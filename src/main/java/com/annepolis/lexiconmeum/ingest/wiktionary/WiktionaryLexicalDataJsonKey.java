package com.annepolis.lexiconmeum.ingest.wiktionary;

public enum WiktionaryLexicalDataJsonKey {

    ARGS("args"),
    CANONICAL("canonical"),
    CONJUGATION("conjugation"),
    DECLENSION("declension"),
    ETYMOLOGY_NUMBER("etymology_number"),
    ETYMOLOGY_TEXT("etymology_text"),
    FORM("form"),
    FORMS("forms"),
    GLOSSES("glosses"),
    HEAD("head"),
    HEAD_TEMPLATES("head_templates"),
    INFLECTION("inflection"),
    NAME("name"),
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
