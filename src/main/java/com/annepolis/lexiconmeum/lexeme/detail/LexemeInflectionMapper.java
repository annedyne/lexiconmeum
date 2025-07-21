package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;

public interface LexemeInflectionMapper<T extends Inflection> {

    InflectionTableDTO toInflectionTableDTO(Lexeme<T> lexeme);
}
