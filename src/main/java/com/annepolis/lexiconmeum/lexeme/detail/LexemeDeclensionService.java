package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class LexemeDeclensionService implements LexemeDetailService {

    private final LexemeDeclensionMapper lexemeDeclensionMapper;
    private final LexemeProvider lexemeProvider;

    public LexemeDeclensionService(LexemeProvider lexemeProvider, LexemeDeclensionMapper lexemeDeclensionMapper){
        this.lexemeDeclensionMapper = lexemeDeclensionMapper;
        this.lexemeProvider = lexemeProvider;
    }
    @Override
    public DeclensionTableDTO getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexeme(lexemeId);
        return lexemeDeclensionMapper.toDeclensionTableDTO(lexeme);
    }
}
