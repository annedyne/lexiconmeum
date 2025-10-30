package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeBuilderTest {

    @Test
    void addInflectionAddsRedundantInflectionFormAsAlternative(){
        String existingForm = "existing";
        Conjugation.Builder conjBuilder = new Conjugation.Builder(existingForm);
        Conjugation existing = conjBuilder.setVoice(GrammaticalVoice.ACTIVE).setMood(GrammaticalMood.INDICATIVE).setTense(GrammaticalTense.PERFECT)
                .setNumber(GrammaticalNumber.SINGULAR).setPerson(GrammaticalPerson.FIRST).build();

        LexemeBuilder underTest = new LexemeBuilder("testLexeme", PartOfSpeech.VERB, "1");
        underTest.addInflection(existing);

        String existingKey = InflectionKey.of(existing);
        Inflection inflection = underTest.getInflections().get(existingKey);
        assertEquals(existing, inflection);
        assertEquals(existingForm, inflection.getForm());

        String alternateForm = "alternate";

        conjBuilder = new Conjugation.Builder(alternateForm);
        Conjugation alternate = conjBuilder.setVoice(GrammaticalVoice.ACTIVE).setMood(GrammaticalMood.INDICATIVE).setTense(GrammaticalTense.PERFECT)
                .setNumber(GrammaticalNumber.SINGULAR).setPerson(GrammaticalPerson.FIRST).build();
        underTest.addInflection(alternate);

        assertEquals(existingForm, underTest.getInflections().get(existingKey).getForm());
        assertEquals(alternateForm, underTest.getInflections().get(existingKey).getAlternativeForm());
    }

    @Test
    void addInflectionAddsResultOfFunctionToLexeme(){
        String existingForm = "existing";
        Conjugation.Builder conjBuilder = new Conjugation.Builder(existingForm);
        Conjugation existing = conjBuilder.setVoice(GrammaticalVoice.ACTIVE).setMood(GrammaticalMood.INDICATIVE).setTense(GrammaticalTense.PERFECT)
                .setNumber(GrammaticalNumber.SINGULAR).setPerson(GrammaticalPerson.FIRST).build();

        LexemeBuilder underTest = new LexemeBuilder("testLexeme", PartOfSpeech.VERB, "1");
        underTest.addInflection(existing);

        String existingKey = InflectionKey.of(existing);
        Inflection inflection = underTest.getInflections().get(existingKey);
        assertEquals(existing, inflection);
        assertEquals(existingForm, inflection.getForm());

        String alternateForm = "alternate";

        underTest.addInflection(existing, existingConj -> existingConj
                .toBuilder()
                .setAlternativeForm(alternateForm)
                .build());

        assertEquals(existingForm, underTest.getInflections().get(existingKey).getForm());
        assertEquals(alternateForm, underTest.getInflections().get(existingKey).getAlternativeForm());
    }
}
