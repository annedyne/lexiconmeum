package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPerson;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class EsseFormProvider {
    private final Map<GrammaticalMood, Map<GrammaticalTense, Map<GrammaticalNumber, Map<GrammaticalPerson, String>>>> esseForms;

    public EsseFormProvider() {
        this.esseForms = initializeForms();
    }

    public String getForm(GrammaticalMood mood, GrammaticalTense tense,
                          GrammaticalNumber number, GrammaticalPerson person) {
        return esseForms
                .getOrDefault(mood, Map.of())
                .getOrDefault(tense, Map.of())
                .getOrDefault(number, Map.of())
                .get(person);
    }

    private Map<GrammaticalMood, Map<GrammaticalTense, Map<GrammaticalNumber, Map<GrammaticalPerson, String>>>> initializeForms() {
        return Map.of(
                GrammaticalMood.INDICATIVE, Map.of(
                        GrammaticalTense.PERFECT, Map.of(
                                GrammaticalNumber.SINGULAR, Map.of(
                                        GrammaticalPerson.FIRST, "sum",
                                        GrammaticalPerson.SECOND, "es",
                                        GrammaticalPerson.THIRD, "est"
                                ),
                                GrammaticalNumber.PLURAL, Map.of(
                                        GrammaticalPerson.FIRST, "sumus",
                                        GrammaticalPerson.SECOND, "estis",
                                        GrammaticalPerson.THIRD, "sunt"
                                )
                        ),
                        GrammaticalTense.PLUPERFECT, Map.of(
                                GrammaticalNumber.SINGULAR, Map.of(
                                        GrammaticalPerson.FIRST, "eram",
                                        GrammaticalPerson.SECOND, "erās",
                                        GrammaticalPerson.THIRD, "erat"
                                ),
                                GrammaticalNumber.PLURAL, Map.of(
                                        GrammaticalPerson.FIRST, "erāmus",
                                        GrammaticalPerson.SECOND, "erātis",
                                        GrammaticalPerson.THIRD, "erant"
                                )
                        ),
                        GrammaticalTense.FUTURE_PERFECT, Map.of(
                                GrammaticalNumber.SINGULAR, Map.of(
                                        GrammaticalPerson.FIRST, "erō",
                                        GrammaticalPerson.SECOND, "eris",
                                        GrammaticalPerson.THIRD, "erit"
                                ),
                                GrammaticalNumber.PLURAL, Map.of(
                                        GrammaticalPerson.FIRST, "erimus",
                                        GrammaticalPerson.SECOND, "eritis",
                                        GrammaticalPerson.THIRD, "erunt"
                                )
                        )
                ),
                GrammaticalMood.SUBJUNCTIVE, Map.of(
                        GrammaticalTense.PERFECT, Map.of(
                                GrammaticalNumber.SINGULAR, Map.of(
                                        GrammaticalPerson.FIRST, "sim",
                                        GrammaticalPerson.SECOND, "sis",
                                        GrammaticalPerson.THIRD, "sit"
                                ),
                                GrammaticalNumber.PLURAL, Map.of(
                                        GrammaticalPerson.FIRST, "sīmus",
                                        GrammaticalPerson.SECOND, "sītis",
                                        GrammaticalPerson.THIRD, "sint"
                                )
                        ),
                        GrammaticalTense.PLUPERFECT, Map.of(
                                GrammaticalNumber.SINGULAR, Map.of(
                                        GrammaticalPerson.FIRST, "essem",
                                        GrammaticalPerson.SECOND, "essēs",
                                        GrammaticalPerson.THIRD, "esset"
                                ),
                                GrammaticalNumber.PLURAL, Map.of(
                                        GrammaticalPerson.FIRST, "essēmus",
                                        GrammaticalPerson.SECOND, "essētis",
                                        GrammaticalPerson.THIRD, "essent"
                                )
                        )
                )
        );
    }
}
