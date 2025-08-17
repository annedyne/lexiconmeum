package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.*;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

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

        Map<GrammaticalPosition, LexemeInflectionMapper> mappers =  new HashMap<>();
        mappers.put(GrammaticalPosition.NOUN, new LexemeDeclensionMapper());
        InflectionTableSectionContributor inflectionTableContributor = new InflectionTableSectionContributor(mappers);
        NounPrincipalPartsSectionContributor nounPrincipalPartsContributor = new NounPrincipalPartsSectionContributor(new InflectionKey());

        Map<GrammaticalPosition, List<LexemeDetailSectionContributor>> pipelines = new HashMap<>();
        pipelines.put(GrammaticalPosition.NOUN, new ArrayList<>());

        pipelines.get(GrammaticalPosition.NOUN).addAll(List.of(
                inflectionTableContributor,
                nounPrincipalPartsContributor
        ));

        LexemeDeclensionService service = new LexemeDeclensionService(lexemeProviderStub, new LexemeDetailPipeline(pipelines));
        UUID lexemeId = getNewTestNounLexeme().getId();
        LexemeDetailResponse dto = service.getLexemeDetail(lexemeId);
        Assertions.assertNotNull(dto.getInflectionTableDTO());
        Assertions.assertEquals(2, dto.getPrincipalParts().size());
    }
}
