package com.annepolis.lexiconmeum.web;

public class ApiRoutes {

    private ApiRoutes() {
        throw new IllegalStateException("Utility class");
    }
    public static final String SEARCH = "/search";
    public static final String PREFIX = SEARCH + "/prefix";
    public static final String SUFFIX = SEARCH + "/suffix";

    public static final String LEXEME = "/lexeme";
    public static final String DETAIL = LEXEME + "/detail";
    public static final String DECLENSION = DETAIL + "/declension";
}
