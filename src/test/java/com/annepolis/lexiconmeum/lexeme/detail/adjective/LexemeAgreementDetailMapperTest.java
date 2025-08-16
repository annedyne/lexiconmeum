package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeAgreementDetailMapperTest {

    @Test
    void secondDeclensionAdjectiveDTOInflectionClassCorrectlyFormed(){
        Lexeme adjective = TestUtil.getNewTestAdjectiveLexeme();
        LexemeAgreementMapper lexemeAgreementMapper = new LexemeAgreementMapper();
        LexemeAgreementDetailMapper underTest = new LexemeAgreementDetailMapper(lexemeAgreementMapper, new InflectionKey() );

       LexemeDetailResponse dto = underTest.toLexemeDetailDTO(adjective);
       assertEquals("1st & 2nd declension", dto.getInflectionClass());
    }
}
