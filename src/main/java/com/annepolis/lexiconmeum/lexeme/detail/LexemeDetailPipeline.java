package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;

import java.util.List;
import java.util.Map;

public class LexemeDetailPipeline {
    private final Map<GrammaticalPosition, List<LexemeDetailSectionContributor>> pipelines;

    public LexemeDetailPipeline(Map<GrammaticalPosition, List<LexemeDetailSectionContributor>> pipelines) {
        this.pipelines = pipelines;
    }

    public LexemeDetailResponse assemble(Lexeme lexeme) {
        var dto = new LexemeDetailResponse();
        var contributors = pipelines.getOrDefault(lexeme.getGrammaticalPosition(), List.of());
        contributors.forEach(c -> c.contribute(lexeme, dto));
        return dto;
    }
}
