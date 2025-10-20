package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

@Component("definitionsSectionContributor")
class DefinitionsSectionContributor implements LexemeDetailSectionContributor {
    @Override
    public boolean supports(Lexeme lexeme) {
        return true;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        lexeme.getSenses().stream()
                .flatMap(s -> s.getGloss().stream())
                .forEach(dto::addDefinition);
    }
}
