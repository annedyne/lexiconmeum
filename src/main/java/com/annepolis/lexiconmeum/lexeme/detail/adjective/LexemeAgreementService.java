package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LexemeAgreementService {
    private final LexemeAgreementDetailMapper lexemeAgreementDetailMapper;
    private final LexemeProvider lexemeProvider;

    public LexemeAgreementService(LexemeProvider lexemeProvider, LexemeAgreementDetailMapper lexemeAgreementDetailMapper){
        this.lexemeAgreementDetailMapper = lexemeAgreementDetailMapper;
        this.lexemeProvider = lexemeProvider;
    }

    public LexemeDetailResponse getLexemeDetail(UUID lexemeId) {
        Lexeme lexeme = lexemeProvider.getLexemeOfType(lexemeId, GrammaticalPosition.ADJECTIVE);
        return lexemeAgreementDetailMapper.toLexemeDetailDTO(lexeme);
    }
}
