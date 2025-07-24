package com.annepolis.lexiconmeum.lexeme.detail.grammar;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import org.apache.logging.log4j.Logger;

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
    CASE_LOCATIVE("locative", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.LOCATIVE);
        }
    }),
    CASE_OBLIQUE("oblique", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.OBLIQUE);
        }
    }),

    NUMBER_SINGULAR("singular", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setNumber(GrammaticalNumber.SINGULAR);
        } else if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setNumber(GrammaticalNumber.SINGULAR);
        }
    }),

    NUMBER_PLURAL("plural", builder -> {
        if (builder instanceof Declension.Builder declBuilder) {
            declBuilder.setNumber(GrammaticalNumber.PLURAL);
        } else if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setNumber(GrammaticalNumber.PLURAL);
        }
    }),

    PERSON_FIRST("first-person", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setPerson(GrammaticalPerson.FIRST);
        }
    }),

    PERSON_SECOND("second-person", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setPerson(GrammaticalPerson.SECOND);
        }
    }),

    PERSON_THIRD("third-person", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setPerson(GrammaticalPerson.THIRD);
        }
    }),

    TENSE_PRESENT("present", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setTense(GrammaticalTense.PRESENT);
        }
    }),
    TENSE_IMPERFECT("imperfect", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setTense(GrammaticalTense.IMPERFECT);
        }
    }),
    TENSE_PERFECT("perfect", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setTense(GrammaticalTense.PERFECT);
        }
    }),
    TENSE_PLUPERFECT("pluperfect", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setTense(GrammaticalTense.PLUPERFECT);
        }
    }),

    TENSE_FUTURE("future", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setTense(GrammaticalTense.FUTURE);
        }
    }),

    TENSE_FUTURE_PERFECT("future_perfect", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setTense(GrammaticalTense.FUTURE_PERFECT);
        }
    }),

    MOOD_INDICATIVE("indicative", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setMood(GrammaticalMood.INDICATIVE);
        }
    }),
    MOOD_SUBJUNCTIVE("subjunctive", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setMood(GrammaticalMood.SUBJUNCTIVE);
        }
    }),
    MOOD_INFINITIVE("infinitive", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setMood(GrammaticalMood.INFINITIVE);
        }
    }),
    MOOD_IMPERATIVE("imperative", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setMood(GrammaticalMood.IMPERATIVE);
        }
    }),

    VOICE_ACTIVE("active", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setVoice(GrammaticalVoice.ACTIVE);
        }
    }),
    VOICE_PASSIVE("passive", builder -> {
        if (builder instanceof Conjugation.Builder conjBuilder) {
            conjBuilder.setVoice(GrammaticalVoice.PASSIVE);
        }
    });


    private final String tag;
    private final Consumer<InflectionBuilder> setter;

    InflectionFeature(String tag, Consumer<InflectionBuilder> setter) {
        this.tag = tag;
        this.setter = setter;
    }

    public void applyTo(InflectionBuilder d) {
        setter.accept(d);
    }

    public static Optional<InflectionFeature> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(inflectionFeature -> inflectionFeature.tag.equalsIgnoreCase(tag))
                .findFirst();
    }

    public static InflectionFeature resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical feature tag: " + tag));
    }

    public static Optional<InflectionFeature> resolveWithWarning(String tag, Logger logger) {
        Optional<InflectionFeature> inflectionFeature = fromTag(tag);
        if (inflectionFeature.isEmpty()) {
            logger.trace("Unknown inflection feature tag: '{}'", tag);
        }
        return inflectionFeature;
    }


}
