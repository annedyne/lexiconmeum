package com.annepolis.lexiconmeum.lexeme.detail;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public enum GrammaticalFeature {

    CASE_NOMINATIVE("nominative", d -> d.setGrammaticalCase(GrammaticalCase.NOMINATIVE)),
    CASE_GENITIVE("genitive", d -> d.setGrammaticalCase(GrammaticalCase.GENITIVE)),
    CASE_DATIVE("dative", d -> d.setGrammaticalCase(GrammaticalCase.DATIVE)),
    CASE_ACCUSATIVE("accusative", d -> d.setGrammaticalCase(GrammaticalCase.ACCUSATIVE)),
    CASE_ABLATIVE("ablative", d -> d.setGrammaticalCase(GrammaticalCase.ABLATIVE)),
    CASE_VOCATIVE("vocative", d -> d.setGrammaticalCase(GrammaticalCase.VOCATIVE)),

    NUMBER_SINGULAR("singular", d -> d.setNumber(GrammaticalNumber.SINGULAR)),
    NUMBER_PLURAL("plural", d -> d.setNumber(GrammaticalNumber.PLURAL)),

    GENDER_MASCULINE("masculine", d -> d.setGender(GrammaticalGender.MASCULINE)),
    GENDER_FEMININE("feminine", d -> d.setGender(GrammaticalGender.FEMININE)),
    GENDER_NEUTER("neuter", d -> d.setGender(GrammaticalGender.NEUTER));

    private final String tag;
    private final Consumer<Declension> setter;

    GrammaticalFeature(String tag, Consumer<Declension> setter) {
        this.tag = tag;
        this.setter = setter;
    }

    public void applyTo(Declension d) {
        setter.accept(d);
    }

    public static Optional<GrammaticalFeature> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(fc -> fc.tag.equalsIgnoreCase(tag))
                .findFirst();
    }
}
