package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;

import java.util.List;
import java.util.Map;

class LexemeDetailResponseAssembler implements LexemeDetailUseCase {
    private final Map<GrammaticalPosition, List<LexemeDetailSectionContributor>> pipelines;

    public LexemeDetailResponseAssembler(Map<GrammaticalPosition, List<LexemeDetailSectionContributor>> pipelines) {
        this.pipelines = pipelines;
    }

    @Override
    public LexemeDetailResponse execute(Lexeme lexeme) {
        var dto = new LexemeDetailResponse();
        var sectionContributors = pipelines.getOrDefault(lexeme.getGrammaticalPosition(), List.of());
        sectionContributors.forEach(c -> c.contribute(lexeme, dto));
        return dto;
    }
}
