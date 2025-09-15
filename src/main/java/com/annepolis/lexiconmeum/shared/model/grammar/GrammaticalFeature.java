package com.annepolis.lexiconmeum.shared.model.grammar;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public enum GrammaticalFeature {

    FIRST(Set.of("declension-1", "conjugation-1"), builder ->
        builder.addInflectionClass(InflectionClass.FIRST)),

    SECOND(Set.of("declension-2", "conjugation-2"), builder ->
        builder.addInflectionClass(InflectionClass.SECOND)),

    THIRD(Set.of("declension-3", "conjugation-3"), builder ->
        builder.addInflectionClass(InflectionClass.THIRD)),

    FOURTH(Set.of("declension-4", "conjugation-4"), builder ->
        builder.addInflectionClass(InflectionClass.FOURTH)),

    FIFTH(Set.of("declension-5"), builder ->
        builder.addInflectionClass(InflectionClass.FIFTH)),

    TWO_TERMINATION(Set.of("two-termination"), builder ->  {
        AdjectiveDetails adjectiveDetails = new AdjectiveDetails(AdjectiveTerminationType.TWO_TERMINATION);
        builder.setPartOfSpeechDetails(adjectiveDetails);
    }),

    THREE_TERMINATION(Set.of("three-termination"), builder ->  {
        AdjectiveDetails adjectiveDetails = new AdjectiveDetails(AdjectiveTerminationType.THREE_TERMINATION);
        builder.setPartOfSpeechDetails(adjectiveDetails);
    });

    private final Set<String> tags;
    private final Consumer<LexemeBuilder> setter;

    GrammaticalFeature(Set<String> tags, Consumer<LexemeBuilder> setter) {
        this.tags = tags;
        this.setter = setter;
    }

    public void applyTo(LexemeBuilder d) {
        setter.accept(d);
    }

    public static Optional<GrammaticalFeature> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(feature -> feature.tags.contains(tag.toLowerCase()))
                .findFirst();
    }

    public static GrammaticalFeature resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical feature tag: " + tag));
    }

    public static Optional<GrammaticalFeature> resolveWithWarning(String tag, Logger logger) {
        Optional<GrammaticalFeature> grammaticalFeature = fromTag(tag);
        if (grammaticalFeature.isEmpty()) {
            logger.trace("Unknown inflection feature tag: '{}'", tag);
        }
        return grammaticalFeature;
    }

}
