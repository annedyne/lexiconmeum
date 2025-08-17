package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Component;

@Component
public class DefinitionsSectionContributor implements LexemeDetailSectionContributor {
    @Override
    public boolean supports(Lexeme lexeme) {
        return true;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        dto.setLemma(lexeme.getLemma());
        lexeme.getSenses().stream()
                .flatMap(s -> s.getGloss().stream())
                .forEach(dto::addDefinition);
    }
}
