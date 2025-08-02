package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
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
