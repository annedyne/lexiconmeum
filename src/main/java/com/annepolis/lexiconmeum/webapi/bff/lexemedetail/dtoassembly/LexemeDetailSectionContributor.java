package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface LexemeDetailSectionContributor {
    boolean supports(Lexeme lexeme);
    void contribute(Lexeme lexeme, LexemeDetailResponse dto);
}
