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
        InflectionKey underTest = new InflectionKey();
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        String key = lexeme.getInflections().stream()
                .filter(i -> i instanceof Conjugation c &&
                        GrammaticalPerson.FIRST == c.getPerson() &&
                        GrammaticalTense.PRESENT == c.getTense() &&
                        GrammaticalVoice.ACTIVE == c.getVoice() &&
                        GrammaticalMood.INDICATIVE == c.getMood() &&
                        GrammaticalNumber.SINGULAR == c.getNumber())
                .map(i -> (Conjugation) i)
                .findFirst()
                .map(c -> underTest.of(c))
                .orElse(null);
        Assertions.assertNotNull( key);
        assertEquals("ACTIVE|INDICATIVE|PRESENT|FIRST|SINGULAR", key);

    }

    @Test
    void buildsValidFirstPrinciplePartKey(){
        InflectionKey builder = new InflectionKey();
        String key = builder.buildFirstPrincipalPartKey();
        assertEquals("ACTIVE|INDICATIVE|PRESENT|FIRST|SINGULAR", key);
    }

    @Test
    void buildsValidSecondPrinciplePartKey(){
        InflectionKey builder = new InflectionKey();
        String key = builder.buildSecondPrincipalPartKey();
        assertEquals("ACTIVE|INFINITIVE|PRESENT", key);
    }

    @Test
    void buildsValidThirdPrinciplePartKey(){
        InflectionKey builder = new InflectionKey();
        String key = builder.buildThirdPrincipalPartKey();
        assertEquals("ACTIVE|INDICATIVE|PERFECT|FIRST|SINGULAR", key);
    }
}
