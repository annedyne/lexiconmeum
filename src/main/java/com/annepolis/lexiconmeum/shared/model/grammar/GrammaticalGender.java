package com.annepolis.lexiconmeum.shared.model.grammar;

import java.util.Arrays;
import java.util.Optional;

public enum GrammaticalGender {
    MASCULINE("masculine"),
    FEMININE("feminine"),
    NEUTER("neuter");

    private final String tag;

    GrammaticalGender(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static Optional<GrammaticalGender> fromTag(String tag) {
        return Arrays.stream(values())
                .filter(fc -> fc.tag.equalsIgnoreCase(tag))
                .findFirst();
    }

    public static GrammaticalGender resolveOrThrow(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unknown grammatical gender: " + tag));
    }
}
