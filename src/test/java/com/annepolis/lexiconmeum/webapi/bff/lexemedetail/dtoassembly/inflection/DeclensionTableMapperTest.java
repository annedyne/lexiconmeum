package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeclensionTableMapperTest {

    @Test
    void toDeclensionTableDTOCorrectlyFormed(){
        DeclensionTableMapper mapper = new DeclensionTableMapper();
        DeclensionTableDTO tableDTO = mapper.toInflectionTableDTO(TestUtil.getNewTestNounLexeme());
        Assertions.assertEquals("amīcus", tableDTO.table.get(GrammaticalNumber.SINGULAR).get(GrammaticalCase.NOMINATIVE));
    }


}
