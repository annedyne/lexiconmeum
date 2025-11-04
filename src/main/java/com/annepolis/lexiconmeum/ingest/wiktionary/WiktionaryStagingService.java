package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class WiktionaryStagingService {

    private static final Logger logger = LogManager.getLogger(WiktionaryStagingService.class);

    private final ParticipleResolutionService participleResolutionService;
    private final StagedLexemeCache stagedLexemeCache;

        public WiktionaryStagingService(ParticipleResolutionService participleResolutionService, StagedLexemeCache stagedLexemeCache){
        this.participleResolutionService = participleResolutionService;
            this.stagedLexemeCache = stagedLexemeCache;
        }

    /**
     * Called after ALL lexemes have been ingested.
     * Performs finalization tasks like attaching staged participles.
     */
    public ParticipleResolutionService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) {
        logger.info("Starting ingestion finalization...");

        int stagedCount = participleResolutionService.getStagedCount();
        logger.info("Found {} staged participles to process", stagedCount);

        ParticipleResolutionService.FinalizationReport report =
                participleResolutionService.finalizeParticiples(lexemeConsumer);

        if (report.hasUnresolved()) {
            logger.warn("Finalization completed with {} unresolved participles",
                    report.getParticiplesUnresolved());

            // Log details
            report.getUnresolvedDetails().forEach((verb, participles) -> {
                logger.warn("  Verb '{}': {} unresolved - {}",
                        verb, participles.size(), participles);
            });
        } else {
            logger.info("All participles successfully resolved");
        }

        return report;
    }

}
