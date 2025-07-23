package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;

public interface LexemeInflectionMapper {

    InflectionTableDTO toInflectionTableDTO(Lexeme lexeme);
}
