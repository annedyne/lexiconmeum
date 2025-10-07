package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.Optional;
import java.util.UUID;

public interface LexemeReader {

    @SuppressWarnings("java:S1452")
    Optional<Lexeme> getLexemeIfPresent(UUID lemmaId);
    Lexeme getLexemeOfType(UUID lemmaId, PartOfSpeech expectedType);

}
