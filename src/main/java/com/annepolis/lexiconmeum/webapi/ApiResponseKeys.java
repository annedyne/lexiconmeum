package com.annepolis.lexiconmeum.webapi;

public class ApiResponseKeys {

    private ApiResponseKeys() {}

    public static final String INFLECTION_TABLE = "inflectionTable";
    public static final String PART_OF_SPEECH = "partOfSpeech";

    // RFC 9457 Problem Details members, used in error responses.
    public static final String PROBLEM_STATUS = "status";
    public static final String PROBLEM_TITLE = "title";
    public static final String PROBLEM_DETAIL = "detail";
    public static final String PROBLEM_INSTANCE = "instance";

    // Custom Problem Details property identifying the lexeme an error refers to.
    public static final String LEXEME_ID = "lexemeId";
}
