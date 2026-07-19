package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompoundInflectionGeneratorTest {

    private final CompoundInflectionGenerator generator =
            new CompoundInflectionGenerator(new EsseFormProvider());

    // Perfect passive participle set for amo, with distinct nominative forms per gender and number.
    private static ParticipleDeclensionSet amatusParticipleSet() {
        return new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT, "amātus")
                .addInflection(nominative("amātus", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE))
                .addInflection(nominative("amāta", GrammaticalNumber.SINGULAR, GrammaticalGender.FEMININE))
                .addInflection(nominative("amātum", GrammaticalNumber.SINGULAR, GrammaticalGender.NEUTER))
                .addInflection(nominative("amātī", GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE))
                .addInflection(nominative("amātae", GrammaticalNumber.PLURAL, GrammaticalGender.FEMININE))
                .addInflection(nominative("amāta", GrammaticalNumber.PLURAL, GrammaticalGender.NEUTER))
                .build();
    }

    private static Participle nominative(String form, GrammaticalNumber number, GrammaticalGender gender) {
        return new Participle.Builder(form)
                .setGrammaticalCase(GrammaticalCase.NOMINATIVE)
                .setNumber(number)
                .addGender(gender)
                .build();
    }

    @Test
    void generatesThreeGendersAcrossSingularAndPlural() {
        List<Conjugation> forms = generator.generateGenderedCompoundForms(
                amatusParticipleSet(),
                GrammaticalVoice.PASSIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT);

        // 3 genders x 2 numbers x 3 persons
        assertEquals(18, forms.size());

        Map<String, String> formsByKey = forms.stream()
                .collect(Collectors.toMap(InflectionKey::of, Conjugation::getForm));

        assertEquals("amātus sum", formsByKey.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|MASCULINE"));
        assertEquals("amāta sum", formsByKey.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|FEMININE"));
        assertEquals("amātum sum", formsByKey.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|NEUTER"));
    }

    @Test
    void pluralUsesNumberSpecificParticipleBase() {
        List<Conjugation> forms = generator.generateGenderedCompoundForms(
                amatusParticipleSet(),
                GrammaticalVoice.PASSIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT);

        Map<String, String> formsByKey = forms.stream()
                .collect(Collectors.toMap(InflectionKey::of, Conjugation::getForm));

        assertEquals("amātī sumus", formsByKey.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL|MASCULINE"));
        assertEquals("amātae sumus", formsByKey.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL|FEMININE"));
    }

    @Test
    void skipsGendersAndNumbersAbsentFromParticipleSet() {
        // Only masculine singular present.
        ParticipleDeclensionSet sparseSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT, "amātus")
                .addInflection(nominative("amātus", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE))
                .build();

        List<Conjugation> forms = generator.generateGenderedCompoundForms(
                sparseSet,
                GrammaticalVoice.PASSIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT);

        // Only the 3 persons of masculine singular.
        assertEquals(3, forms.size());
        forms.forEach(f -> {
            assertEquals(GrammaticalGender.MASCULINE, f.getGender());
            assertEquals(GrammaticalNumber.SINGULAR, f.getNumber());
        });
    }
}
