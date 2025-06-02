package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LexemeDeclensionMapperTest {

    @Test
    void toDeclensionTableDTOCorrectlyFormed(){
        LexemeDeclensionMapper mapper = new LexemeDeclensionMapper();
        DeclensionTableDTO tableDTO = mapper.toDeclensionTableDTO(TestUtil.getNewTestNounLexeme());
        Assertions.assertEquals("amīcus", tableDTO.table.get(GrammaticalNumber.SINGULAR).get(GrammaticalCase.NOMINATIVE));
    }


}
