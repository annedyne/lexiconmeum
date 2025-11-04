package com.annepolis.lexiconmeum.ingest.wiktionary;

public enum WiktionaryLexicalDataKeyWord {
    TEMPLATE_HEAD_VERB("la-verb"),
    TEMPLATE_HEAD_PARTICPLE("la-part");

    private final String keyWord;

    WiktionaryLexicalDataKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String get() {
        return keyWord;
    }
}
