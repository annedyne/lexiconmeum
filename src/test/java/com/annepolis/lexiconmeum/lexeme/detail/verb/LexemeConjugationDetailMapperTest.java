package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeConjugationDetailMapperTest {

    @Test
    void allPrincipalPartsAreMapped(){
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        LexemeInflectionMapper mapperStub = lexeme1 -> null;
        LexemeConjugationDetailMapper underTest = new LexemeConjugationDetailMapper(mapperStub, new InflectionKey());
        LexemeDetailResponse dto = underTest.toLexemeDetailDTO(lexeme);
        assertEquals(3,dto.getPrincipalParts().size());
    }
}
