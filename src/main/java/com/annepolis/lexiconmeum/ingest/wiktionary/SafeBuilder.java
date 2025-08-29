package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class SafeBuilder<T> {
    private final Supplier<T> builder;
    private final String label;

    SafeBuilder(String label, Supplier<T> builder) {
        this.label = label;
        this.builder = builder;
    }

    Optional<T> build(Logger logger, ParseMode mode) {
        try {
            return Optional.ofNullable(builder.get());
        } catch (Exception e) {
            if (mode == ParseMode.LENIENT) {
                logger.trace("Skipping {} due to build error: {}", label, e.getMessage());
                return Optional.empty();
            } else {
                throw e;
            }
        }
    }
}
