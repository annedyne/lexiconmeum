package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;

import java.util.Optional;
import java.util.UUID;

public interface LexemeProvider {

    @SuppressWarnings("java:S1452")
    Optional<Lexeme<?>> getLexemeIfPresent(UUID lemmaId);
    <T extends Inflection> Lexeme<T> getLexemeOfType(UUID lemmaId, Class<T> expectedType);

}
