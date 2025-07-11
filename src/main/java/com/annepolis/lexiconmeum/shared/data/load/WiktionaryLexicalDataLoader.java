package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Component
class WiktionaryLexicalDataLoader implements LexemeSinkLoader {

    public static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataLoader.class);

    private final WiktionaryLexicalDataParser parser;
    private final Resource lexicalData;
    private final List<LexemeSink> lexemeSinks;

    public WiktionaryLexicalDataLoader(List<LexemeSink> lexemeSinks, WiktionaryLexicalDataParser parser, LoadProperties loadProperties) {
        this.parser = parser;
        this.parser.setParseMode(loadProperties.getParseMode());
        this.lexicalData = loadProperties.getDataFile();
        this.lexemeSinks = lexemeSinks;
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {

        try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
            logger.info("Initiating lexical data load");
            parser.parseJsonl(reader, lexeme -> {
               for(LexemeSink sink : getLexemeSinks()){
                   sink.accept(lexeme);
                }
            });
            logger.info("Finished lexical data load");
        }
    }


    @Override
    public List<LexemeSink> getLexemeSinks() {
        return lexemeSinks;
    }
}
