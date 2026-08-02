package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.IngestLexemeUseCase;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class WiktionaryLexicalDataLoader {

    public static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataLoader.class);

    private final IngestLexemeUseCase ingestLexemeUseCase;
    private final WiktionaryLexicalDataParser parser;
    private final WiktionaryLexicalDataEntrySource entrySource;
    private final DefaultWiktionaryStagingService wiktionaryStagingService;


    public WiktionaryLexicalDataLoader(IngestLexemeUseCase ingestLexemeUseCase,
                                       WiktionaryLexicalDataParser parser,
                                       WiktionaryLexicalDataEntrySource entrySource,
                                       DefaultWiktionaryStagingService wiktionaryStagingService

    ) {
        this.ingestLexemeUseCase = ingestLexemeUseCase;
        this.parser = parser;
        this.entrySource = entrySource;
        this.wiktionaryStagingService = wiktionaryStagingService;
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {
        logger.info("PHASE 1: Staging lexemes from Wiktionary data");
        
        // Stage all lexemes (no distribution yet)
        entrySource.readResolvedEntries(entry -> parser.processJson(entry, ingestLexemeUseCase::ingest));
        
        logger.info("PHASE 2: Finalizing linkable data and distributing complete lexemes");
        
        // Finalize Linking and distribute everything once
        DataLinkingService.FinalizationReport report = wiktionaryStagingService.finalizeIngestion(ingestLexemeUseCase::ingest);

        logger.info("Load complete: {}", report.getSummary());

        if (report.hasUnresolved()) {
            logger.warn("Some linkable parent lemmas could not be resolved:");
            report.unresolvedDetails().forEach((parentLemma, linkables) ->
                    logger.warn(" {}: {}", parentLemma, String.join(", ", linkables))
            );
        }
    }

}
