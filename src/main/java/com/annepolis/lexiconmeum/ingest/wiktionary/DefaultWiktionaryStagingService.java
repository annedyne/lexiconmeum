package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Service responsible for caching non-head-words and potential parent lexemes pending finalization.
 * Finalization tasks including associating participles with parent verbs
 * and comparative and superlative adjectives with positive parent adjectives
 * are performed once all lexemes have been ingested.
 */
@Component
public class DefaultWiktionaryStagingService implements WiktionaryStagingService{

    private static final Logger logger = LogManager.getLogger(DefaultWiktionaryStagingService.class);

    private final DataLinkingService dataLinkingService;
    private final StagedLexemeCache stagedLexemeCache;

    public DefaultWiktionaryStagingService(DataLinkingService dataLinkingService,
                                           StagedLexemeCache stagedLexemeCache) {
        this.dataLinkingService = dataLinkingService;
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
    public void stageLinkableData(LinkableData dataToLink) {
        dataLinkingService.stageDataToLink(dataToLink);
    }

    private void clearStaged(){
        stagedLexemeCache.clearStaged();
    }

    /**
     * Called after ALL lexemes have been ingested.
     * Performs finalization tasks like attaching staged participles.
     */
    @Override
    public DataLinkingService.FinalizationReport finalizeIngestion(Consumer<Lexeme> lexemeConsumer) {
        logger.info("Starting ingestion finalization...");

        int stagedCount = dataLinkingService.getStagedCount();
        logger.info("Found {} staged lexical entities to process", stagedCount);

        DataLinkingService.FinalizationReport report =
                dataLinkingService.finalizeLexicalDataLinking(lexemeConsumer, stagedLexemeCache);

        if (report.hasUnresolved()) {
            logger.warn("Finalization completed with {} unresolved non-lexeme entities",
                    report.linkablesUnresolved());

            // Log details
            report.unresolvedDetails().forEach((lexeme, linkables) -> logger.warn(" Parent {}: {} unresolved - {}",
                    lexeme, linkables.size(), linkables));
        } else {
            logger.info("All participles successfully resolved");
        }
        clearStaged();
        return report;
    }
}
