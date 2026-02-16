package com.annepolis.lexiconmeum.shared.model.grammar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrammaticalParticipleTenseTest {

    @Test
    void getDisplayName_andAlternativeName_returnConfiguredValues() {
        // A couple of spot checks (including the non-obvious ones)
        assertEquals("Participle", GrammaticalParticipleTense.PARTICIPLE.getDisplayName());
        assertEquals("Participle", GrammaticalParticipleTense.PARTICIPLE.getAlternativeName());

        // PERFECT_ACTIVE has intentionally different display vs alternative naming
        assertEquals("Perfect Passive Participle", GrammaticalParticipleTense.PERFECT_ACTIVE.getDisplayName());
        assertEquals("Perfect Active Participle", GrammaticalParticipleTense.PERFECT_ACTIVE.getAlternativeName());

        // FUTURE_PASSIVE display name is "Gerundive"
        assertEquals("Gerundive", GrammaticalParticipleTense.FUTURE_PASSIVE.getDisplayName());
        assertEquals("Future Passive Participle", GrammaticalParticipleTense.FUTURE_PASSIVE.getAlternativeName());
    }

    @Test
    void fromVoiceAndTense_mapsValidCombinations() {
        assertEquals(
                GrammaticalParticipleTense.PRESENT_ACTIVE,
                GrammaticalParticipleTense.fromVoiceAndTense(GrammaticalVoice.ACTIVE, GrammaticalTense.PRESENT)
        );

        assertEquals(
                GrammaticalParticipleTense.FUTURE_ACTIVE,
                GrammaticalParticipleTense.fromVoiceAndTense(GrammaticalVoice.ACTIVE, GrammaticalTense.FUTURE)
        );

        assertEquals(
                GrammaticalParticipleTense.PERFECT_PASSIVE,
                GrammaticalParticipleTense.fromVoiceAndTense(GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT)
        );

        assertEquals(
                GrammaticalParticipleTense.FUTURE_PASSIVE,
                GrammaticalParticipleTense.fromVoiceAndTense(GrammaticalVoice.PASSIVE, GrammaticalTense.FUTURE)
        );

        // For deponent perfect passive: ACTIVE + PERFECT -> PERFECT_ACTIVE
        assertEquals(
                GrammaticalParticipleTense.PERFECT_ACTIVE,
                GrammaticalParticipleTense.fromVoiceAndTense(GrammaticalVoice.ACTIVE, GrammaticalTense.PERFECT)
        );
    }

    @Test
    void fromVoiceAndTense_throwsForInvalidCombination() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> GrammaticalParticipleTense.fromVoiceAndTense(GrammaticalVoice.PASSIVE, GrammaticalTense.PRESENT)
        );

        assertTrue(ex.getMessage().startsWith("Invalid participle combination: "));
        assertTrue(ex.getMessage().contains("PASSIVE"));
        assertTrue(ex.getMessage().contains("PRESENT"));
    }

    @Test
    void tryFromVoiceAndTense_returnsOptionalForValidAndInvalidCombinations() {
        assertTrue(
                GrammaticalParticipleTense.tryFromVoiceAndTense(GrammaticalVoice.ACTIVE, GrammaticalTense.PRESENT)
                        .isPresent()
        );
        assertEquals(
                GrammaticalParticipleTense.PRESENT_ACTIVE,
                GrammaticalParticipleTense.tryFromVoiceAndTense(GrammaticalVoice.ACTIVE, GrammaticalTense.PRESENT)
                        .orElseThrow()
        );

        assertFalse(
                GrammaticalParticipleTense.tryFromVoiceAndTense(GrammaticalVoice.PASSIVE, GrammaticalTense.PRESENT)
                        .isPresent()
        );
    }
}
