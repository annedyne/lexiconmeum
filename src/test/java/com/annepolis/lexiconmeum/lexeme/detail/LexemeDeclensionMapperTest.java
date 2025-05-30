package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LexemeDeclensionMapperTest {

    @Test
    void toDeclensionTableDTOCorrectlyFormed(){
        LexemeDeclensionMapper mapper = new LexemeDeclensionMapper();
        DeclensionTableDTO tableDTO = mapper.toDeclensionTableDTO(TestUtil.getNewTestNounLexeme());
        Assertions.assertEquals("amīcus", tableDTO.table.get(GrammaticalNumber.SINGULAR.name()).get(GrammaticalCase.NOMINATIVE.name()));
    }


}
