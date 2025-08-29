package com.annepolis.lexiconmeum.shared.model.grammar;

import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InflectionFeatureTest {

    @Test
    void grammaticalCaseAppliedToGramCaseBuilder(){
        Set<String> forms = new LinkedHashSet<>();

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
