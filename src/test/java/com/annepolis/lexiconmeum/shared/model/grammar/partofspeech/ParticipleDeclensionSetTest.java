package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParticipleDeclensionSetTest {

    @Test
    public void secondFormForSameInflectionKeyGoesToAlt() {

        // Create two participle inflections with the same key but different forms
        String mainForm = "amante";
        Participle.Builder pBuilder = new Participle.Builder(mainForm);
        Participle participleMain = pBuilder.setGrammaticalCase(GrammaticalCase.ABLATIVE)
                .addGender(GrammaticalGender.FEMININE)
                .setDegree(GrammaticalDegree.POSITIVE)
                .setNumber(GrammaticalNumber.SINGULAR)
                .build();

        String altForm = "amantī";
        Participle.Builder pBuilder2 = new Participle.Builder(altForm);
        Participle participleAlt = pBuilder2.setGrammaticalCase(GrammaticalCase.ABLATIVE)
                .addGender(GrammaticalGender.FEMININE)
                .setDegree(GrammaticalDegree.POSITIVE)
                .setNumber(GrammaticalNumber.SINGULAR)
                .build();

        // Add them to a list
        List<Participle> inflections = new ArrayList<>();
        inflections.add(participleMain);
        inflections.add(participleAlt);

        // Create a new builder for the set
        ParticipleDeclensionSet.Builder participleSetBuilder = new ParticipleDeclensionSet.Builder(
                GrammaticalVoice.ACTIVE,
                GrammaticalTense.PRESENT,
                "amans"
        );

        String key = InflectionKey.of(participleMain);
        // Add the duplicate key inflections
        ParticipleDeclensionSet participleDeclensionSet = participleSetBuilder.addInflections(inflections).build();

        // Retrieve the first (main) inflection
        Inflection participle = participleDeclensionSet.getInflectionIndex().get(key);

        // Verify that both forms are in their appropriate respective fields.
        assertEquals(mainForm, participle.getForm() );
        assertEquals(altForm, participle.getAlternativeForm());

    }
}
