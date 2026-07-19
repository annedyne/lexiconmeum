package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPerson;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds compound (participle + esse) verb forms for the perfect-system tenses.
 * A single participle base form expands across every number/person of the given
 * mood and tense. Gender is carried onto each generated form so gendered variants
 * (masculine, feminine, neuter) remain distinct inflections; pass null for the
 * ungendered baseline.
 */
@Component
public class CompoundInflectionGenerator {

    private final EsseFormProvider esseFormProvider;

    public CompoundInflectionGenerator(EsseFormProvider esseFormProvider) {
        this.esseFormProvider = esseFormProvider;
    }

    // Expand one participle base across all number/person combinations of the mood and tense.
    public List<Conjugation> generateCompoundForms(
            String participleBase,
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalGender gender) {

        List<Conjugation> compoundForms = new ArrayList<>();
        for (GrammaticalNumber number : GrammaticalNumber.values()) {
            for (GrammaticalPerson person : GrammaticalPerson.values()) {
                String esseForm = esseFormProvider.getForm(mood, tense, number, person);
                String compoundForm = participleBase + " " + esseForm;

                compoundForms.add(new Conjugation.Builder(compoundForm)
                        .setVoice(voice)
                        .setMood(mood)
                        .setTense(tense)
                        .setNumber(number)
                        .setPerson(person)
                        .setGender(gender)
                        .build());
            }
        }
        return compoundForms;
    }
}
