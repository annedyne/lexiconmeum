package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.NounDetails;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public enum PartOfSpeechDetailFactory {

    GENDER_FEMININE(Set.of("feminine"), builder -> {
        NounDetails nounDetails = new NounDetails(GrammaticalGender.FEMININE);
        builder.setPartOfSpeechDetails(nounDetails);
    }),

    GENDER_MASCULINE(Set.of("masculine"), builder ->  {
        NounDetails nounDetails = new NounDetails(GrammaticalGender.MASCULINE);
        builder.setPartOfSpeechDetails(nounDetails);
    }),

    GENDER_NEUTER(Set.of("neuter"), builder ->  {
        NounDetails nounDetails = new NounDetails(GrammaticalGender.NEUTER);
        builder.setPartOfSpeechDetails(nounDetails);
    }),

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

    PartOfSpeechDetailFactory(Set<String> tags, Consumer<LexemeBuilder> setter) {
        this.tags = tags;
        this.setter = setter;
    }

    public void applyTo(LexemeBuilder d) {
        setter.accept(d);
    }

    public static Optional<PartOfSpeechDetailFactory> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(feature -> feature.tags.contains(tag.toLowerCase()))
                .findFirst();
    }

    public static PartOfSpeechDetailFactory resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical feature tag: " + tag));
    }

    public static Optional<PartOfSpeechDetailFactory> resolveWithWarning(String tag, Logger logger) {
        Optional<PartOfSpeechDetailFactory> partOfSpeechDetailFactory = fromTag(tag);
        if (partOfSpeechDetailFactory.isEmpty()) {
            logger.trace("Unknown inflection feature tag: '{}'", tag);
        }
        return partOfSpeechDetailFactory;
    }
}
