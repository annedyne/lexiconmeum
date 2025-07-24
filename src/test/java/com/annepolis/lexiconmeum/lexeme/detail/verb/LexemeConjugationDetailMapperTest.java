package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeConjugationDetailMapperTest {

    @Test
    void allPrinciplePartsAreMapped(){
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        LexemeInflectionMapper mapperStub = new LexemeInflectionMapper() {
            @Override
            public InflectionTableDTO toInflectionTableDTO(Lexeme lexeme) {
                return null;
            }
        };
        LexemeConjugationDetailMapper underTest = new LexemeConjugationDetailMapper(mapperStub, new InflectionKey());
        LexemeConjugationDetailDTO dto = underTest.toLexemeDetailDTO(lexeme);
        assertEquals(3,dto.getPrincipleParts().size());
    }
}
