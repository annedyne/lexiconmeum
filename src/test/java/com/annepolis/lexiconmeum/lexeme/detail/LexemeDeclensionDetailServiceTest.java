package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LexemeDeclensionDetailServiceTest {

    @Test
    void getLexemeDetailGivenLexemeReturnsDeclensionMap(){
        LexemeDeclensionDetailService service = new LexemeDeclensionDetailService(new LexemeDeclensionMapper());

        DeclensionTableDTO dto = service.getLexemeDetail(TestUtil.getNewTestLexeme());
        Assertions.assertNotNull(dto.getTable());
    }
}
