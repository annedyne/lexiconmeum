package com.annepolis.lexiconmeum.shared.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class LexemeNotFoundException extends NoSuchElementException {
    private final UUID lexemeId;

    public LexemeNotFoundException(UUID lexemeId) {
        super("Lexeme not found: " + lexemeId);
        this.lexemeId = lexemeId;
    }

    public UUID getLexemeId() {
        return lexemeId;
    }
}

