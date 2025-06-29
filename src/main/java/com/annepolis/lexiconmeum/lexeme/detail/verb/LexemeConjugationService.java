package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class LexemeConjugationService {

    private final LexemeConjugationMapper lexemeConjugationMapper;
    private final LexemeProvider lexemeProvider;

    public LexemeConjugationService(LexemeProvider lexemeProvider, LexemeConjugationMapper lexemeConjugationMapper){
        this.lexemeConjugationMapper = lexemeConjugationMapper;
        this.lexemeProvider = lexemeProvider;
    }

    public ConjugationGroupDTO getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexeme(lexemeId);
        return lexemeConjugationMapper.toInflectionTableDTO(lexeme);
    }

}
