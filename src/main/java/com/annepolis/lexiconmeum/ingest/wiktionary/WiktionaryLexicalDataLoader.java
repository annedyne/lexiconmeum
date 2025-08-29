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



    public WiktionaryLexicalDataLoader(IngestLexemeUseCase ingestLexemeUseCase,
                                       WiktionaryLexicalDataParser parser,
                                       LoadProperties loadProperties
    ) {
        this.ingestLexemeUseCase = ingestLexemeUseCase;
        this.parser = parser;
        this.parser.setParseMode(loadProperties.getParseMode());
        this.lexicalData = loadProperties.getDataFile();
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {

        try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
            logger.info("Initiating lexical data load");
            parser.parseJsonl(reader, lexeme -> {
                ingestLexemeUseCase.ingest(lexeme);
            });
            logger.info("Finished lexical data load");
        }
    }

}
