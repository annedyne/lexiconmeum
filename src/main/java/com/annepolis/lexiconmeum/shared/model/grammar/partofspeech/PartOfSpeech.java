package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.InflectionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

import static com.annepolis.lexiconmeum.shared.model.grammar.InflectionType.*;

public enum PartOfSpeech {
    ADJECTIVE("adj", DECLENSION),
    ADVERB("adv", NONE),
    CONJUNCTION("conj", NONE),
    DETERMINER("det", DECLENSION),
    NOUN("noun", DECLENSION),
    PREPOSITION("prep", NONE),
    POSTPOSITION("postp", NONE),
    PRONOUN("pron", DECLENSION),
    VERB("verb", CONJUGATION);


    private final String tag;
    private final InflectionType inflectionType;

    PartOfSpeech(String tag, InflectionType inflectionType) {
        this.tag = tag;
        this.inflectionType = inflectionType;
    }

    public InflectionType getInflectionType() {
        return inflectionType;
    }

    public String getInflectionTypeLower(){
        return StringUtils.toRootLowerCase(this.inflectionType.name());
    }

    public String getTag(){
       return tag;
    }

    public static Optional<PartOfSpeech> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(pos -> pos.tag.equalsIgnoreCase(tag))
                .findFirst();
    }

    public static PartOfSpeech resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown partOfSpeech: " + tag));
    }

    public static Optional<PartOfSpeech> resolveWithWarning(String tag, Logger logger) {
        Optional<PartOfSpeech> partOfSpeech = fromTag(tag);
        if (partOfSpeech.isEmpty()) {
            logger.trace("Unknown partOfSpeech tag: '{}'", tag);
        }
        return partOfSpeech;
    }
}
