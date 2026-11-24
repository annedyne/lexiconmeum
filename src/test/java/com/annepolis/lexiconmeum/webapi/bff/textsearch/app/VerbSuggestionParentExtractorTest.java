package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerbSuggestionParentExtractorTest {

    private final VerbSuggestionParentExtractor underTest = new VerbSuggestionParentExtractor(new InflectionKey());

    @Test
    void returnsSecondPrincipalPartWhenPresent() {
        Conjugation infinitive = new Conjugation.Builder("amāre")
                .setVoice(GrammaticalVoice.ACTIVE)
                .setMood(GrammaticalMood.INFINITIVE)
                .setTense(GrammaticalTense.PRESENT)
                .build();
        Lexeme lexeme = new LexemeBuilder("amo", PartOfSpeech.VERB, "1")
                .addInflection(infinitive)
                .build();

        assertEquals(Optional.of("amāre"), underTest.getSuggestionParent(lexeme));
    }

    @Test
    void returnsEmptyWhenSecondPrincipalPartMissing() {
        Lexeme lexeme = new LexemeBuilder("amo", PartOfSpeech.VERB, "1").build();

        assertTrue(underTest.getSuggestionParent(lexeme).isEmpty());
    }
}
