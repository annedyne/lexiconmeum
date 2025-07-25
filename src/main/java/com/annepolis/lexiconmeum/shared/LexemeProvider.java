package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;

import java.util.Optional;
import java.util.UUID;

public interface LexemeProvider {

    @SuppressWarnings("java:S1452")
    Optional<Lexeme> getLexemeIfPresent(UUID lemmaId);
    Lexeme getLexemeOfType(UUID lemmaId, GrammaticalPosition expectedType);

}
