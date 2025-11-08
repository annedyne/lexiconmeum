package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.IngestLexemeUseCase;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Component
class WiktionaryLexicalDataLoader {

    public static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataLoader.class);

    private final IngestLexemeUseCase ingestLexemeUseCase;
    private final WiktionaryLexicalDataParser parser;
    private final Resource lexicalData;
    private final DefaultWiktionaryStagingService wiktionaryStagingService;


    public WiktionaryLexicalDataLoader(IngestLexemeUseCase ingestLexemeUseCase,
                                       WiktionaryLexicalDataParser parser,
                                       LoadProperties loadProperties, DefaultWiktionaryStagingService wiktionaryStagingService

    ) {
        this.ingestLexemeUseCase = ingestLexemeUseCase;
        this.parser = parser;
        this.wiktionaryStagingService = wiktionaryStagingService;
        this.parser.setParseMode(loadProperties.getParseMode());
        this.lexicalData = loadProperties.getDataFile();
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {
    try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
        logger.info("PHASE 1: Staging lexemes from Wiktionary data");
        
        // Stage all lexemes (no distribution yet)
        parser.parseJsonl(reader, ingestLexemeUseCase::ingest);
        
        logger.info("PHASE 2: Finalizing participles and distributing complete lexemes");
        
        // Finalize participles and distribute everything once
        ParticipleResolutionService.FinalizationReport report = wiktionaryStagingService.finalizeIngestion(ingestLexemeUseCase::ingest);

        logger.info("Load complete: {}", report.getSummary());

        if (report.hasUnresolved()) {
            logger.warn("Some participles could not be resolved:");
            report.getUnresolvedDetails().forEach((verb, participles) ->
                    logger.warn("  Verb '{}': {}", verb, String.join(", ", participles))
            );
        }
    }
}

}
