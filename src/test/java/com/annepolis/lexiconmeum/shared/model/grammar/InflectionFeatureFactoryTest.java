package com.annepolis.lexiconmeum.shared.model.grammar;

import com.annepolis.lexiconmeum.ingest.tagmapping.InflectionFeatureFactory;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InflectionFeatureFactoryTest {

    @Test
    void grammaticalCaseAppliedToGramCaseBuilder(){

        Agreement.Builder builder = new Agreement.Builder("pulcher");
        InflectionFeatureFactory.resolveOrThrow("nominative").applyTo(builder);
        assertEquals(GrammaticalCase.NOMINATIVE, builder.getGrammaticalCase());
    }

    @Test
    void grammaticalCaseNotAppliedToNonGramCaseBuilder(){

        Conjugation.Builder builder = new Conjugation.Builder("pulcher");
        assertDoesNotThrow(() ->
                InflectionFeatureFactory.resolveOrThrow("nominative").applyTo(builder)
        );
    }
}
