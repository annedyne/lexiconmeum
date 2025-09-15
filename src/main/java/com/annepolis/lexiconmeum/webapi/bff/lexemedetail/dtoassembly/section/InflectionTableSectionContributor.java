package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.InflectionTableMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("inflectionTableSectionContributor")
class InflectionTableSectionContributor implements LexemeDetailSectionContributor {
    private final Map<GrammaticalPosition, InflectionTableMapper> mappers;

    public InflectionTableSectionContributor(Map<GrammaticalPosition, InflectionTableMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return mappers.containsKey(lexeme.getPartOfSpeech());
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        dto.setInflectionTableDTO(mappers.get(lexeme.getPartOfSpeech()).toInflectionTableDTO(lexeme));
    }

}

