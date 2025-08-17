package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Component;

@Component
public class IdentitySectionContributor implements LexemeDetailSectionContributor {
    @Override
    public boolean supports(Lexeme lexeme) { return true; }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        dto.setLexemeId(lexeme.getId());
        dto.setPosition(lexeme.getGrammaticalPosition());
        dto.setLemma(lexeme.getLemma());
    }
}

