package com.annepolis.lexiconmeum.shared.model;

import java.util.function.Consumer;

public interface LexemeSink extends Consumer<Lexeme> {

    void accept(Lexeme lexeme);
}


