package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailPipeline;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class LexemeConjugationService {

    private final LexemeProvider lexemeProvider;
    private final LexemeDetailPipeline assembler;

    public LexemeConjugationService(LexemeProvider lexemeProvider, LexemeDetailPipeline assembler){
        this.lexemeProvider = lexemeProvider;
        this.assembler = assembler;
    }

    public LexemeDetailResponse getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexemeOfType(lexemeId, GrammaticalPosition.VERB);
        return assembler.assemble(lexeme);
    }

}
