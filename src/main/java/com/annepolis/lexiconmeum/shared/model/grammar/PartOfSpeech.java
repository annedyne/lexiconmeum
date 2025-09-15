package com.annepolis.lexiconmeum.shared.model.grammar;

import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

public enum PartOfSpeech {
    NOUN("noun", "declension"),
    VERB("verb", "conjugation"),
    ADVERB("adv", ""),
    ADJECTIVE("adj", "declension");
    private final String tag;
    private final String inflectionType;

    PartOfSpeech(String tag, String inflectionType) {
        this.tag = tag;
        this.inflectionType = inflectionType;
    }

    public String getInflectionType() {
        return inflectionType;
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
