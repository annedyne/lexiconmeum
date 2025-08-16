package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionFeature;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InflectionFeatureTest {

    @Test
    void grammaticalCaseAppliedToGramCaseBuilder(){

        Agreement.Builder builder = new Agreement.Builder("pulcher");
        InflectionFeature.resolveOrThrow("nominative").applyTo(builder);
        assertEquals(GrammaticalCase.NOMINATIVE, builder.getGrammaticalCase());
    }

    @Test
    void grammaticalCaseNotAppliedToNonGramCaseBuilder(){

        Conjugation.Builder builder = new Conjugation.Builder("pulcher");
        assertDoesNotThrow(() ->
                InflectionFeature.resolveOrThrow("nominative").applyTo(builder)
        );
    }
}
