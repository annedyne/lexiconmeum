package com.annepolis.lexiconmeum.ingest.wiktionary;


public enum WiktionaryHeadTemplate {
    VERB("la-verb"),
    PARTICIPLE("la-part"),
    ADJECTIVE_POSITIVE("la-adj"),
    ADJECTIVE_COMPARATIVE("la-adj-comp"),
    ADJECTIVE_SUPERLATIVE("la-adj-sup");


    private final String name ;

    WiktionaryHeadTemplate(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
