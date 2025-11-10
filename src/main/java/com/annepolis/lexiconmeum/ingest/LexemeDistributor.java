package com.annepolis.lexiconmeum.ingest;

import com.annepolis.lexiconmeum.ingest.wiktionary.ParticipleResolutionService;
import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for managing the staging and distribution of {@link Lexeme} objects
 * during the ingestion process. It implements the {@link IngestLexemeUseCase} interface to provide
 * functionality for staging lexemes, finalizing lexical data (e.g., participle attachment),
 * and distributing finalized lexemes to a list of {@link LexemeSink} consumers.
 *
 * This class operates with a thread-safe internal buffer of staged lexemes, enabling updates and
 * proper synchronization during ingestion and distribution phases.
 *
 * This class consists of two primary phases for processing lexemes:
 * 1. Staging phase: During this phase, lexemes are buffered for potential updates and not distributed.
 * 2. Finalization and distribution phase: Lexemes are finalized using the {@link ParticipleResolutionService},
 *    after which they are distributed to registered sinks.
 *
 * Fail-fast behavior is employed to handle errors during distribution to ensure proper error visibility.
 *
 * Dependencies:
 * - {@link ParticipleResolutionService}: Handles participle attachment and final data preparation.
 * - {@link LexemeSink}: Consumes finalized lexemes.
 *
 * Thread Safety:
 * - The internal map of staged lexemes is implemented using {@link ConcurrentHashMap}, ensuring
 *   thread-safe operations during staging and finalization.
 */
@Service
public class LexemeDistributor implements IngestLexemeUseCase {
    private static final Logger logger = LoggerFactory.getLogger(LexemeDistributor.class);
    
    private final List<LexemeSink> sinks;

    public LexemeDistributor(List<LexemeSink> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void ingest(Lexeme lexeme) {
        distributeToSinks(lexeme);
    }


    
    /**
     * Internal method to distribute a single lexeme to all sinks
     */
    private void distributeToSinks(Lexeme lexeme) {
        for (LexemeSink sink : sinks) {
            try {
                sink.accept(lexeme);
            } catch (Exception e) {
                logger.error("Error distributing lexeme {} to sink {}", 
                        lexeme.getId(), sink.getClass().getSimpleName(), e);
                throw e; // Current behavior: fail-fast
            }
        }
    }




}
