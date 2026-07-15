package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefinitionsSectionContributorTest {

    private final DefinitionsSectionContributor contributor = new DefinitionsSectionContributor();

    private Lexeme lexeme(String shortDefinition, Sense... senses) {
        LexemeBuilder builder = new LexemeBuilder("test", PartOfSpeech.VERB, "1");
        builder.setShortDefinition(shortDefinition);
        for (Sense sense : senses) {
            builder.addSense(sense);
        }
        return builder.build();
    }

    @Test
    void shortDefinitionIsPassedThroughFromLexeme() {
        Sense sense = new Sense.Builder()
                .addGloss("(literally):")
                .addGloss("to love, like; to be fond of")
                .build();

        LexemeDetailResponse dto = new LexemeDetailResponse();
        contributor.contribute(lexeme("to love, like; to be fond of", sense), dto);

        assertEquals("to love, like; to be fond of", dto.getShortDefinition());
    }

    @Test
    void shortDefinitionIsNullWhenNotSetOnLexeme() {
        Sense sense = new Sense.Builder().addGloss("to love").build();

        LexemeDetailResponse dto = new LexemeDetailResponse();
        contributor.contribute(lexeme(null, sense), dto);

        assertNull(dto.getShortDefinition());
    }
}