package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefinitionsSectionContributorTest {

    private final DefinitionsSectionContributor contributor = new DefinitionsSectionContributor();

    private Lexeme lexemeWithSenses(Sense... senses) {
        LexemeBuilder builder = new LexemeBuilder("test", PartOfSpeech.VERB, "1");
        for (Sense sense : senses) {
            builder.addSense(sense);
        }
        return builder.build();
    }

    @Test
    void shortDefinitionIsLeafOfFirstSensePath() {
        Sense sense = new Sense.Builder()
                .addGloss("(literally):")
                .addGloss("to love, like; to be fond of")
                .build();

        LexemeDetailResponse dto = new LexemeDetailResponse();
        contributor.contribute(lexemeWithSenses(sense), dto);

        assertEquals("to love, like; to be fond of", dto.getShortDefinition());
    }

    @Test
    void shortDefinitionComesFromFirstNonEmptySense() {
        Sense first = new Sense.Builder()
                .addGloss("(literally):")
                .addGloss("to love")
                .build();
        Sense second = new Sense.Builder()
                .addGloss("(figurative):")
                .addGloss("to cherish")
                .build();

        LexemeDetailResponse dto = new LexemeDetailResponse();
        contributor.contribute(lexemeWithSenses(first, second), dto);

        assertEquals("to love", dto.getShortDefinition());
    }
}