package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.TestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeConjugationMapperTest {

    @Test
    void toConjugationTableMapsExpectedNumberOfForms(){
        LexemeConjugationMapper mapper = new LexemeConjugationMapper();
        ConjugationTableDTO tableDTO = mapper.toInflectionTableDTO(TestUtil.getNewTestVerbLexeme());
        assertEquals(7, tableDTO.getTenses().get(0).getForms().size());
    }
}
