package com.annepolis.lexiconmeum.lexeme.detail.grammar;

import java.util.Arrays;
import java.util.Optional;

public enum GrammaticalPosition {
    NOUN("noun"),
    VERB("verb");

    private final String tag;

    GrammaticalPosition(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
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
}
