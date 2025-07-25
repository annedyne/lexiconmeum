package com.annepolis.lexiconmeum.web;

public class ApiRoutes {

    private ApiRoutes() {
        throw new IllegalStateException("Utility class");
    }

    public static final String LEXEMES = "/lexemes";
    public static final String AUTOCOMPLETE = "/autocomplete";
    public static final String PREFIX = LEXEMES + AUTOCOMPLETE + "/prefix";
    public static final String SUFFIX = LEXEMES + AUTOCOMPLETE + "/suffix";

    public static final String LEXEME_DETAIL = LEXEMES + "/{id}/detail";
}
