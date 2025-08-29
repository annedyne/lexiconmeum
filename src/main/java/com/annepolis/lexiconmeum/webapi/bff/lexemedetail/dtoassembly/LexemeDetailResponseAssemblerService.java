package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface LexemeDetailResponseAssemblerService {

    LexemeDetailResponse assemble(Lexeme lexeme);
}
