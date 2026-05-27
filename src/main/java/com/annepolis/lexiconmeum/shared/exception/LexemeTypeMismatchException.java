package com.annepolis.lexiconmeum.shared.exception;

import java.util.UUID;

public class LexemeTypeMismatchException extends RuntimeException {
    private final UUID lexemeId;

    public LexemeTypeMismatchException(String message, UUID lexemeId) {
        super(lexemeId != null
                ? "Type mismatch for lexeme " + lexemeId + ": " + message
                : "Type mismatch: " + message);
        this.lexemeId = lexemeId;
    }

    public UUID getLexemeId() {
        return lexemeId;
    }
}
