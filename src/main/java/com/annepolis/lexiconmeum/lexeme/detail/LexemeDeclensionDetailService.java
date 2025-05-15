package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;
import org.springframework.stereotype.Service;

@Service
public class LexemeDeclensionDetailService implements LexemeDeclensionDetailComponent {

    private LexemeDeclensionMapper lexemeDeclensionMapper;

    public LexemeDeclensionDetailService(LexemeDeclensionMapper lexemeDeclensionMapper){
        this.lexemeDeclensionMapper = lexemeDeclensionMapper;
    }
    @Override
    public DeclensionTableDTO getLexemeDetail(Lexeme lexeme) {
        return lexemeDeclensionMapper.toDeclensionTableDTO(lexeme);
    }
}
