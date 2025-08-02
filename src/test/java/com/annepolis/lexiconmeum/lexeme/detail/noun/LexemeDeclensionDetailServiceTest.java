package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionKey;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static com.annepolis.lexiconmeum.TestUtil.getNewTestNounLexeme;

class LexemeDeclensionDetailServiceTest {

    @Test
    void getLexemeDetailGivenLexemeReturnsDeclensionDetail(){
        LexemeProvider lexemeProviderStub = new LexemeProvider() {

            @Override
            public Optional<Lexeme> getLexemeIfPresent(UUID lemmaId) {
                return Optional.empty();
            }

            @Override
            public Lexeme getLexemeOfType(UUID lemmaId, GrammaticalPosition expectedType) {
                return TestUtil.getNewTestNounLexeme();
            }
        };

        LexemeDeclensionService service = new LexemeDeclensionService(lexemeProviderStub, new LexemeDeclensionDetailMapper(new LexemeDeclensionMapper(),new InflectionKey()));
        UUID lexemeId = getNewTestNounLexeme().getId();
        LexemeDetailResponse dto = service.getLexemeDetail(lexemeId);
        Assertions.assertNotNull(dto.getInflectionTableDTO());
        Assertions.assertEquals(2, dto.getPrincipalParts().size());
    }
}
