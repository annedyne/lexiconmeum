package com.annepolis.lexiconmeum.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilitiesTest {

    @Test
    void removeMacronsNormalizesStringAsExpected(){
        String normalized = Utilities.normalizeDiacritics("āēīōū");
        assertEquals("aeiou", normalized);
    }
}
