package com.annepolis.lexiconmeum.ingest.wiktionary;

/**
 * A Wiktionary head template includes
 * the lua script function and parameters
 * for a given word.
 */
public enum WiktionaryHeadTemplate {
    ADJECTIVE_POSITIVE("la-adj"),
    ADJECTIVE_COMPARATIVE("la-adj-comp"),
    ADJECTIVE_SUPERLATIVE("la-adj-sup"),
    ADVERB("la-adv"),
    CONJUNCTION("conjunction" ), // conjunctions seem only to have head_templates.name head - no 'la-conj'
    DETERMINER("la-det"),
    PREPOSITION("la-prep"),
    POSTPOSITION("la-postp"),
    PRONOUN("la-pronoun"), // in 'qui' pronoun entries there is 'head' and 'la-pronoun' and I only want 'la-pronoun'
    NOUN("la-noun"),
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
