package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.inflection.*;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public enum InflectionFeatureFactory {

    CASE_NOMINATIVE("nominative", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase caseBuilder) {
            caseBuilder.setGrammaticalCase(GrammaticalCase.NOMINATIVE);
        }
    }),
    CASE_DATIVE("dative", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.DATIVE);
        }
    }),
    CASE_ACCUSATIVE("accusative", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.ACCUSATIVE);
        }
    }),
    CASE_GENITIVE("genitive", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.GENITIVE);
        }
    }),

    CASE_ABLATIVE("ablative", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.ABLATIVE);
        }
    }),
    CASE_VOCATIVE("vocative", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.VOCATIVE);
        }
    }),
    CASE_LOCATIVE("locative", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.LOCATIVE);
        }
    }),
    CASE_OBLIQUE("oblique", builder -> {
        if (builder instanceof BuilderHasGrammaticalCase declBuilder) {
            declBuilder.setGrammaticalCase(GrammaticalCase.OBLIQUE);
        }
    }),

    GENDER_FEMININE("feminine", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder) {
            agrBuilder.addGender(GrammaticalGender.FEMININE);
        } else if (builder instanceof Participle.Builder agrBuilder) {
            agrBuilder.addGender(GrammaticalGender.FEMININE);
        }
    }),

    GENDER_MASCULINE("masculine", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder) {
            agrBuilder.addGender(GrammaticalGender.MASCULINE);
        } else if (builder instanceof Participle.Builder agrBuilder) {
            agrBuilder.addGender(GrammaticalGender.MASCULINE);
        }
    }),

    GENDER_NEUTER("neuter", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder){
            agrBuilder.addGender(GrammaticalGender.NEUTER);
        } else if (builder instanceof Participle.Builder agrBuilder) {
            agrBuilder.addGender(GrammaticalGender.NEUTER);
        }
    }),

    DEGREE_POSITIVE("positive", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder){
            agrBuilder.setDegree(GrammaticalDegree.POSITIVE);
        } else if (builder instanceof Participle.Builder agrBuilder) {
            agrBuilder.setDegree(GrammaticalDegree.POSITIVE);
        }
    }),

    DEGREE_COMPARATIVE("comparative", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder){
            agrBuilder.setDegree(GrammaticalDegree.COMPARATIVE);
        }  else if (builder instanceof Participle.Builder agrBuilder) {
            agrBuilder.setDegree(GrammaticalDegree.COMPARATIVE);
        }
    }),

    DEGREE_SUPERLATIVE("superlative", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder){
            agrBuilder.setDegree(GrammaticalDegree.SUPERLATIVE);
        }  else if (builder instanceof Participle.Builder agrBuilder) {
            agrBuilder.setDegree(GrammaticalDegree.SUPERLATIVE);
        }
    }),

    ADVERB("adverb", builder -> {
        if (builder instanceof Agreement.Builder agrBuilder){
            agrBuilder.setAdverb(true);
        }
    }),

    NUMBER_SINGULAR("singular", builder ->
        builder.setNumber(GrammaticalNumber.SINGULAR)),

    NUMBER_PLURAL("plural", builder ->
        builder.setNumber(GrammaticalNumber.PLURAL)),

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

    PARTICIPLE_PRESENT_ACTIVE("present_participle", builder -> {
        if(builder instanceof Conjugation.Builder conjBuilder){
            conjBuilder.setParticiple(GrammaticalParticiple.PRESENT_ACTIVE);
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

    InflectionFeatureFactory(String tag, Consumer<InflectionBuilder> setter) {
        this.tag = tag;
        this.setter = setter;
    }

    public void applyTo(InflectionBuilder d) {
        setter.accept(d);
    }

    public static Optional<InflectionFeatureFactory> fromTag(String tag) {
        return Arrays.stream(values())
            .filter(inflectionFeatureFactory -> inflectionFeatureFactory.tag.equalsIgnoreCase(tag))
            .findFirst();
    }

    public static InflectionFeatureFactory resolveOrThrow(String tag) {
        return fromTag(tag)
            .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical feature tag: " + tag));
    }

    public static Optional<InflectionFeatureFactory> resolveWithWarning(String tag, Logger logger) {
        Optional<InflectionFeatureFactory> inflectionFeature = fromTag(tag);
        if (inflectionFeature.isEmpty()) {
            logger.trace("Unknown inflection feature tag: '{}'", tag);
        }
        return inflectionFeature;
    }


}
