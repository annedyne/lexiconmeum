package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface InflectionTableMapper {

    InflectionTableDTO toInflectionTableDTO(Lexeme lexeme);
}
