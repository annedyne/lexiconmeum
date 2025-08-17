package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface LexemeDetailSectionContributor {
    boolean supports(Lexeme lexeme);
    void contribute(Lexeme lexeme, LexemeDetailResponse dto);
}
