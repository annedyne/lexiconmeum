package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public enum InflectionClassFactory {

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

    IRREGULAR(Set.of("irreg"), builder ->
            builder.addInflectionClass(InflectionClass.FIFTH));

    private final Set<String> tags;
    private final Consumer<LexemeBuilder> setter;

    InflectionClassFactory(Set<String> tags, Consumer<LexemeBuilder> setter) {
        this.tags = tags;
        this.setter = setter;
    }

    public void applyTo(LexemeBuilder d) {
        setter.accept(d);
    }

    public static Optional<InflectionClassFactory> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(feature -> feature.tags.contains(tag.toLowerCase()))
                .findFirst();
    }

    public static InflectionClassFactory resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical feature tag: " + tag));
    }

    public static Optional<InflectionClassFactory> resolveWithWarning(String tag, Logger logger) {
        Optional<InflectionClassFactory> grammaticalFeature = fromTag(tag);
        if (grammaticalFeature.isEmpty()) {
            logger.trace("Unknown inflection feature tag: '{}'", tag);
        }
        return grammaticalFeature;
    }

}
