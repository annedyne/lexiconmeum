package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.function.Consumer;

public interface LexemeSink extends Consumer<Lexeme> {

    void accept(Lexeme lexeme);
}


