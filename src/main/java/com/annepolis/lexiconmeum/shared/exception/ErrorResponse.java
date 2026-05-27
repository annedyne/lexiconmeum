package com.annepolis.lexiconmeum.shared.exception;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String lexemeId
) { }



