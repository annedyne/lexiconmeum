package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Component
class WiktionaryLexicalDataLoader implements LexemeSinkLoader {

    private final WiktionaryLexicalDataParser parser;
    private final Resource lexicalData;
    private final List<LexemeSink> lexemeSinks;

    public WiktionaryLexicalDataLoader(List<LexemeSink> lexemeSinks, WiktionaryLexicalDataParser parser, @Value("${latin.data-file}")
    Resource dataFile) {
        this.parser = parser;
        this.lexicalData = dataFile;
        this.lexemeSinks = lexemeSinks;
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {
        try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
            parser.parseJsonl(reader, lexeme -> {
               for(LexemeSink sink : getLexemeSinks()){
                   sink.accept(lexeme);
                }
            });
        }
    }


    @Override
    public List<LexemeSink> getLexemeSinks() {
        return lexemeSinks;
    }
}
