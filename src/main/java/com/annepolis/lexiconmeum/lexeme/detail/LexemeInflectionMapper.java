package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface LexemeInflectionMapper {

    InflectionTableDTO toInflectionTableDTO(Lexeme lexeme);
}
