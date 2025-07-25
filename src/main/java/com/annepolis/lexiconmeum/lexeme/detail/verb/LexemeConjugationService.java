package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class LexemeConjugationService {

    private final LexemeConjugationDetailMapper lexemeConjugationDetailMapper;
    private final LexemeProvider lexemeProvider;

    public LexemeConjugationService(LexemeProvider lexemeProvider, LexemeConjugationDetailMapper lexemeConjugationDetailMapper){
        this.lexemeConjugationDetailMapper = lexemeConjugationDetailMapper;
        this.lexemeProvider = lexemeProvider;
    }

    public LexemeDetailResponse getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexemeOfType(lexemeId, GrammaticalPosition.VERB);
        return lexemeConjugationDetailMapper.toLexemeDetailDTO(lexeme);

    }

}
