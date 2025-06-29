package com.annepolis.lexiconmeum.shared;

import java.util.Optional;
import java.util.UUID;

public interface LexemeProvider {

    Lexeme getLexeme(UUID lemma);

    Optional<Lexeme> getLexemeIfPresent(UUID lemmaId);
}
