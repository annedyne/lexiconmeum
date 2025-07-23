package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LexemeDeclensionService {

    private final LexemeDeclensionMapper lexemeDeclensionMapper;
    private final LexemeProvider lexemeProvider;

    public LexemeDeclensionService(LexemeProvider lexemeProvider, LexemeDeclensionMapper lexemeDeclensionMapper){
        this.lexemeDeclensionMapper = lexemeDeclensionMapper;
        this.lexemeProvider = lexemeProvider;
    }

    public DeclensionTableDTO getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexemeOfType(lexemeId, Declension.class);
        return lexemeDeclensionMapper.toInflectionTableDTO(lexeme);
    }
}
