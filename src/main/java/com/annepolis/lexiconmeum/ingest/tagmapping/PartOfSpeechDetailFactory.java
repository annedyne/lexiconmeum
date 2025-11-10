package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

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
    }),

    DEMONSTRATIVE(Set.of("demonstrative"), builder ->  {
        // determiners of demonstrative subtype have three gender termination types
        DeterminerDetails determinerDetails = new DeterminerDetails(SyntacticSubtype.DEMONSTRATIVE);
        builder.setPartOfSpeechDetails(determinerDetails);
    }),

    RELATIVE(Set.of("relative"), builder ->  {
        PronounDetails pronounDetails = new PronounDetails(SyntacticSubtype.RELATIVE);
        builder.setPartOfSpeechDetails(pronounDetails);
    }),

    INTERROGATIVE(Set.of("interrogative"), builder ->  {
        DeterminerDetails determinerDetails = new DeterminerDetails(SyntacticSubtype.INTERROGATIVE);
        builder.setPartOfSpeechDetails(determinerDetails);
    }),

    GOVERNED_CASE_ACCUSATIVE(Set.of("with-accusative"), builder ->  {
        if( PartOfSpeech.PREPOSITION == builder.getPartOfSpeech() || PartOfSpeech.POSTPOSITION == builder.getPartOfSpeech() ) {
            PrepositionDetails prepositionDetails = new PrepositionDetails(GrammaticalCase.ACCUSATIVE);
            builder.setPartOfSpeechDetails(prepositionDetails);
        } else {
            LogManager.getLogger(PartOfSpeechDetailFactory.class).debug(MarkerManager.getMarker("POS_FACTORY"),
                    "gov case accusative lemma: {} pos : {} ", builder::getLemma, builder::getPartOfSpeech );
        }
    }),

    GOVERNED_CASE_ABLATIVE(Set.of("with-ablative"), builder ->  {
        if( PartOfSpeech.PREPOSITION == builder.getPartOfSpeech() || PartOfSpeech.POSTPOSITION == builder.getPartOfSpeech()) {
            PrepositionDetails prepositionDetails = new PrepositionDetails(GrammaticalCase.ABLATIVE);
            builder.setPartOfSpeechDetails(prepositionDetails);
        } else {
            LogManager.getLogger(PartOfSpeechDetailFactory.class).debug(MarkerManager.getMarker("POS_FACTORY"),
                    "gov case ablative lemma: {} pos : {} ", builder::getLemma, builder::getPartOfSpeech );
        }
    }),

    GOVERNED_CASE_GENITIVE(Set.of("with-genitive"), builder ->  {
        if( PartOfSpeech.PREPOSITION == builder.getPartOfSpeech() || PartOfSpeech.POSTPOSITION == builder.getPartOfSpeech()) {
            PrepositionDetails prepositionDetails = new PrepositionDetails(GrammaticalCase.GENITIVE);
            builder.setPartOfSpeechDetails(prepositionDetails);
        } else {
            LogManager.getLogger(PartOfSpeechDetailFactory.class).debug(MarkerManager.getMarker("POS_FACTORY"),
                    "gov case genitive lemma: {} pos : {} ", builder::getLemma, builder::getPartOfSpeech );
        }
    }),

    GOVERNED_CASE_DATIVE(Set.of("with-dative"), builder ->  {
        if( PartOfSpeech.PREPOSITION == builder.getPartOfSpeech() || PartOfSpeech.POSTPOSITION == builder.getPartOfSpeech()) {
            PrepositionDetails prepositionDetails = new PrepositionDetails(GrammaticalCase.DATIVE);
            builder.setPartOfSpeechDetails(prepositionDetails);
        } else {
            LogManager.getLogger(PartOfSpeechDetailFactory.class).debug(MarkerManager.getMarker("POS_FACTORY"),
                    "gov case dative pos : {} ", builder::getPartOfSpeech );

        }
    }),

    DEPONENT(Set.of("deponent"), builder ->  {
       if( builder.getPartOfSpeechDetails() instanceof VerbDetails details) {
           details.toBuilder().setMorphologicalSubtype(MorphologicalSubtype.DEPONENT);
           builder.setPartOfSpeechDetails(details);
       } else if(builder.getPartOfSpeechDetails() == null){
           VerbDetails.Builder vdBuilder = new VerbDetails.Builder();
           vdBuilder.setMorphologicalSubtype(MorphologicalSubtype.DEPONENT);
           builder.setPartOfSpeechDetails(vdBuilder.build());
       }
    });

    private static final Logger logger = LogManager.getLogger(PartOfSpeechDetailFactory.class);

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
