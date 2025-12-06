package com.annepolis.lexiconmeum.ingest.wiktionary;

import java.util.Set;

public class ParserConstants {

    public static final Set<String> COMMON_FORM_BLACKLIST = Set.of(
            "no-table-tags",
            "la-ndecl",
            "conjugation-1",
            "la-conj",
            "la-adecl",
            "two-termination",
            "sigmatic"
    );
}
