package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface LexemeDetailUseCase {

    LexemeDetailResponse execute(Lexeme lexeme);
}
