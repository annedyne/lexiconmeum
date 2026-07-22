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
}
