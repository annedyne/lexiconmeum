package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.*;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InflectionKeyTest {

    @Test
    void buildsValidInflectionKeyFromConjugation(){
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        String key = lexeme.getInflections().stream()
                .filter(Conjugation.class::isInstance)
                .map(i -> (Conjugation) i)
                .filter(i ->
                        GrammaticalPerson.FIRST == i.getPerson() &&
                        GrammaticalTense.PRESENT == i.getTense() &&
                        GrammaticalVoice.ACTIVE == i.getVoice() &&
                        GrammaticalMood.INDICATIVE == i.getMood() &&
                        GrammaticalNumber.SINGULAR == i.getNumber())
                .findFirst()
                .map(InflectionKey::of)
                .orElse(null);
        Assertions.assertNotNull( key);
        assertEquals("ACTIVE|INDICATIVE|PRESENT|FIRST|SINGULAR", key);

    }

    @Test
    void buildsValidFirstPrincipalPartKey(){
        InflectionKey builder = new InflectionKey();
        String key = builder.buildFirstPrincipalPartKey();
        assertEquals("ACTIVE|INDICATIVE|PRESENT|FIRST|SINGULAR", key);
    }

    @Test
    void buildsValidSecondPrincipalPartKey(){
        InflectionKey builder = new InflectionKey();
        String key = builder.buildSecondPrincipalPartKey();
        assertEquals("ACTIVE|INFINITIVE|PRESENT", key);
    }

    @Test
    void buildsValidThirdPrincipalPartKey(){
        InflectionKey builder = new InflectionKey();
        String key = builder.buildThirdPrincipalPartKey();
        assertEquals("ACTIVE|INDICATIVE|PERFECT|FIRST|SINGULAR", key);
    }
}
