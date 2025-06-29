package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

class LexemeDeclensionDetailServiceTest {

    @Test
    void getLexemeDetailGivenLexemeReturnsDeclensionMap(){
        LexemeProvider lexemeProviderStub = new LexemeProvider() {
            @Override
            public Lexeme getLexeme(UUID lemma) {
                return TestUtil.getNewTestNounLexeme();
            }

            @Override
            public Optional<Lexeme> getLexemeIfPresent(UUID lemmaId) {
                return Optional.empty();
            }
        };
        LexemeDeclensionService service = new LexemeDeclensionService(lexemeProviderStub, new LexemeDeclensionMapper());
        UUID lexemeId = TestUtil.getNewTestNounLexeme().getId();
        DeclensionTableDTO dto = service.getLexemeDetail(lexemeId);
        Assertions.assertNotNull(dto.getTable());
    }
}
