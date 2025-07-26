package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LexemeDeclensionService {

    private final LexemeDeclensionDetailMapper lexemeDeclensionDetailMapper;
    private final LexemeProvider lexemeProvider;

    public LexemeDeclensionService(LexemeProvider lexemeProvider, LexemeDeclensionDetailMapper lexemeDeclensionDetailMapper){
        this.lexemeDeclensionDetailMapper = lexemeDeclensionDetailMapper;
        this.lexemeProvider = lexemeProvider;
    }

    public LexemeDetailResponse getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexemeOfType(lexemeId, GrammaticalPosition.NOUN);
        return lexemeDeclensionDetailMapper.toLexemeDetailDTO(lexeme);
    }
}
