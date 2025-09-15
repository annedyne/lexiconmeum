package com.annepolis.lexiconmeum.shared.model.grammar;

import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

public enum GrammaticalPosition {
    NOUN("noun", "declension"),
    VERB("verb", "conjugation"),
    ADVERB("adv", ""),
    ADJECTIVE("adj", "declension");
    private final String tag;
    private final String inflectionType;

    GrammaticalPosition(String tag, String inflectionType) {
        this.tag = tag;
        this.inflectionType = inflectionType;
    }

    public String getTag() {
        return tag;
    }

    public String getInflectionType() {
        return inflectionType;
    }

    public static Optional<GrammaticalPosition> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(pos -> pos.tag.equalsIgnoreCase(tag))
                .findFirst();
    }

    public static GrammaticalPosition resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical position: " + tag));
    }

    public static Optional<GrammaticalPosition> resolveWithWarning(String tag, Logger logger) {
        Optional<GrammaticalPosition> position = fromTag(tag);
        if (position.isEmpty()) {
            logger.trace("Unknown grammatical position tag: '{}'", tag);
        }
        return position;
    }
}
