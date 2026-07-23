package com.annepolis.lexiconmeum.ingest.wiktionary;

import java.util.Map;
import java.util.Set;

public class ParserConstants {

    private ParserConstants(){}

    public static final Set<String> COMMON_FORM_BLACKLIST = Set.of(
            "no-table-tags",
            "la-ndecl",
            "conjugation-1",
            "la-conj",
            "la-adecl",
            "two-termination",
            "sigmatic",
            "lg" // 'ego has this no idea what it is'
    );

    // Suppletive plural personal pronouns keyed to the singular parent lemma
    // whose plural agreement section they populate. This closed set is why
    // detection is an explicit map rather than tag/gloss inference.
    public static final Map<String, String> SUPPLETIVE_PRONOUN_PARENTS = Map.of(
            "nos", "ego",
            "vos", "tu"
    );

    // Reflexive pronoun lemmas whose entries Wiktionary tags as a non-lemma
    // "pronoun form" head template, so they resolve to no parser key and are
    // skipped. Flagged by name so parser-key derivation rescues them as PRONOUN.
    public static final Set<String> REFLEXIVE_PRONOUN_LEMMAS = Set.of(
            "sui"
    );

    // The non-lemma head-template name that flagged reflexive pronouns carry.
    // Gating the rescue on this leaves the noun/verb form entries on the same
    // page ("noun form" / "verb form") correctly skipped.
    public static final String PRONOUN_FORM_HEAD_TEMPLATE = "pronoun form";
}
