package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.function.Consumer;

/**
 * Encapsulates the action to be taken with parsed data.
 * Allows parser methods to define the appropriate processor
 * for the given parsed output.
 */
@FunctionalInterface
public interface ParsedResultProcessor {

    void process(Consumer<Lexeme> lexemeConsumer, WiktionaryStagingService stagingService);

    ParsedResultProcessor EMPTY = (l, s) -> {};
}
