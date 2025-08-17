package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;

import java.util.Map;

public class InflectionTableSectionContributor implements LexemeDetailSectionContributor {
    private final Map<GrammaticalPosition, LexemeInflectionMapper> mappers;

    public InflectionTableSectionContributor(Map<GrammaticalPosition, LexemeInflectionMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return mappers.containsKey(lexeme.getGrammaticalPosition());
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        dto.setInflectionTableDTO(mappers.get(lexeme.getGrammaticalPosition()).toInflectionTableDTO(lexeme));
    }

}

