package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class LexemeDeclensionDetailServiceTest {

    @Test
    void getLexemeDetailGivenLexemeReturnsDeclensionMap(){
        LexemeProvider lexemeProvider = lemma -> TestUtil.getNewTestNounLexeme();
        LexemeDeclensionService service = new LexemeDeclensionService(lexemeProvider, new LexemeDeclensionMapper());
        UUID lexemeId = TestUtil.getNewTestNounLexeme().getId();
        DeclensionTableDTO dto = service.getLexemeDetail(lexemeId);
        Assertions.assertNotNull(dto.getTable());
    }
}
