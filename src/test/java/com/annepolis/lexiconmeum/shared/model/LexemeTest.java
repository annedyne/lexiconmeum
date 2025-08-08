package com.annepolis.lexiconmeum.shared.model;

import nl.jqno.equalsverifier.EqualsVerifier;

public class LexemeTest {

    @org.junit.jupiter.api.Test
    void equalsContract() {
        EqualsVerifier.forClass(Lexeme.class)
                .usingGetClass()
                .withIgnoredFields("id", "inflectionClasses", "senses", "inflections")
                .verify();
    }
}
