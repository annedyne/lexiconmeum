package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.exception.LexemeTypeMismatchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static com.annepolis.lexiconmeum.TestUtil.getNewTestNounLexeme;

class LexemeDeclensionDetailServiceTest {

    @Test
    void getLexemeDetailGivenLexemeReturnsDeclensionMap(){
        LexemeProvider lexemeProviderStub = new LexemeProvider() {

            @Override
            public Optional<Lexeme> getLexemeIfPresent(UUID lemmaId) {

                return Optional.empty();
            }

            @Override
            public <T extends Inflection> Lexeme getLexemeOfType(UUID lemmaId, Class<T> expectedType) {

                Lexeme lexeme = TestUtil.getNewTestNounLexeme();
                boolean matches = lexeme.getInflections().stream().allMatch(expectedType::isInstance);
                if (!matches) {
                    throw new LexemeTypeMismatchException("Expected lexeme of type " + expectedType.getSimpleName());
                }
                return lexeme;
            }
        };
        LexemeDeclensionService service = new LexemeDeclensionService(lexemeProviderStub, new LexemeDeclensionMapper());
        UUID lexemeId = getNewTestNounLexeme().getId();
        DeclensionTableDTO dto = service.getLexemeDetail(lexemeId);
        Assertions.assertNotNull(dto.getTable());
    }
}
