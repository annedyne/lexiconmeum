package com.annepolis.lexiconmeum.lexeme.detail.grammar;

import com.annepolis.lexiconmeum.lexeme.detail.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public enum InflectionFeature {

    CASE_NOMINATIVE("nominative", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.NOMINATIVE);
        }
    }),
    CASE_DATIVE("dative", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.DATIVE);
        }
    }),
    CASE_ACCUSATIVE("accusative", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.ACCUSATIVE);
        }
    }),
    CASE_GENITIVE("genitive", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.GENITIVE);
        }
    }),

    CASE_ABLATIVE("ablative", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.ABLATIVE);
        }
    }),
    CASE_VOCATIVE("vocative", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.VOCATIVE);
        }
    }),

    NUMBER_SINGULAR("singular", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setNumber(GrammaticalNumber.SINGULAR);
        }
    }),

    NUMBER_PLURAL("plural", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setNumber(GrammaticalNumber.PLURAL);
        }
    });


    private final String tag;
    private final Consumer<InflectionBuilder<?>> setter;

    InflectionFeature(String tag, Consumer<InflectionBuilder<?>> setter) {
        this.tag = tag;
        this.setter = setter;
    }

    public void applyTo(InflectionBuilder<?> d) {
        setter.accept(d);
    }

    public static Optional<InflectionFeature> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(inflectionFeature -> inflectionFeature.tag.equalsIgnoreCase(tag))
                .findFirst();
    }
}
