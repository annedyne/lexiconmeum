package com.annepolis.lexiconmeum.shared;

import java.util.UUID;

public interface LexemeProvider {

    Lexeme getLexeme(UUID lemma);
}
