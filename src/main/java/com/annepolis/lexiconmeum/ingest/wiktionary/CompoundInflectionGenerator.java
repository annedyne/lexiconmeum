package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPerson;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * Builds the gendered compound forms of one perfect-system tense from a
     * participle declension set. For each gender the nominative singular and
     * plural participle forms supply the number-specific base, so plurals agree
     * (e.g. amātī sumus, not amātus sumus). Genders or numbers absent from the
     * set are skipped.
     */
    public List<Conjugation> generateGenderedCompoundForms(
            ParticipleDeclensionSet participleSet,
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense) {

        List<Conjugation> compoundForms = new ArrayList<>();
        for (GrammaticalGender gender : GrammaticalGender.values()) {
            for (GrammaticalNumber number : GrammaticalNumber.values()) {
                Optional<String> base = findNominativeForm(participleSet, gender, number);
                if (base.isEmpty()) {
                    continue;
                }
                compoundForms.addAll(generateForNumber(base.get(), voice, mood, tense, number, gender));
            }
        }
        return compoundForms;
    }

    // Combine one number-specific participle base with the esse forms for every person.
    private List<Conjugation> generateForNumber(
            String participleBase,
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalNumber number,
            GrammaticalGender gender) {

        List<Conjugation> compoundForms = new ArrayList<>();
        for (GrammaticalPerson person : GrammaticalPerson.values()) {
            String esseForm = esseFormProvider.getForm(mood, tense, number, person);
            compoundForms.add(new Conjugation.Builder(participleBase + " " + esseForm)
                    .setVoice(voice)
                    .setMood(mood)
                    .setTense(tense)
                    .setNumber(number)
                    .setPerson(person)
                    .setGender(gender)
                    .build());
        }
        return compoundForms;
    }

    // Find the nominative participle form for the given gender and number.
    private Optional<String> findNominativeForm(
            ParticipleDeclensionSet participleSet,
            GrammaticalGender gender,
            GrammaticalNumber number) {

        return participleSet.getInflectionIndex().values().stream()
                .filter(Participle.class::isInstance)
                .map(Participle.class::cast)
                .filter(participle -> participle.getGrammaticalCase() == GrammaticalCase.NOMINATIVE)
                .filter(participle -> participle.getNumber() == number)
                .filter(participle -> participle.getGenders().contains(gender))
                .map(Participle::getForm)
                .findFirst();
    }
}
