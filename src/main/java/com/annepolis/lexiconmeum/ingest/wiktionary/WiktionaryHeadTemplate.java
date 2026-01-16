package com.annepolis.lexiconmeum.ingest.wiktionary;


public enum WiktionaryHeadTemplate {
    VERB("la-verb"),
    PARTICIPLE("la-part");

    private final String name ;

    WiktionaryHeadTemplate(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
