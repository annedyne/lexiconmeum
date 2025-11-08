package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Service responsible for caching participles and potential parent verb lexemes pending finalization.
 * Finalization tasks including associating participles with parent verbs
 * are performed once all lexemes have been ingested.
 */
@Component
public class DefaultWiktionaryStagingService implements WiktionaryStagingService{

    private static final Logger logger = LogManager.getLogger(DefaultWiktionaryStagingService.class);

    private final ParticipleResolutionService participleResolutionService;
    private final StagedLexemeCache stagedLexemeCache;

    public DefaultWiktionaryStagingService(ParticipleResolutionService participleResolutionService,
                                           StagedLexemeCache stagedLexemeCache) {
        this.participleResolutionService = participleResolutionService;
        this.stagedLexemeCache = stagedLexemeCache;
    }

    /**
     * Stage a complete lexeme (typically non-verb lexemes or verbs without participles yet)
     */
    @Override
    public void stageLexeme(Lexeme lexeme) {
        stagedLexemeCache.putLexeme(lexeme);
    }

    /**
     * Stage participle data for later resolution
     */
    @Override
    public void stageParticiple(StagedParticipleData participleData) {
        participleResolutionService.stageParticiple(participleData);
    }

    /**
     * Called after ALL lexemes have been ingested.
     * Performs finalization tasks like attaching staged participles.
     */
    @Override
    public ParticipleResolutionService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) {
        logger.info("Starting ingestion finalization...");

        int stagedCount = participleResolutionService.getStagedCount();
        logger.info("Found {} staged participles to process", stagedCount);

        ParticipleResolutionService.FinalizationReport report =
                participleResolutionService.finalizeParticiples(lexemeConsumer, stagedLexemeCache);

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
