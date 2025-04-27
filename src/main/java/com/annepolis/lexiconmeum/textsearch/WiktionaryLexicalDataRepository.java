package com.annepolis.lexiconmeum.textsearch;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Consumer;

@Component
class WiktionaryLexicalDataRepository {

    private final WiktionaryLexicalDataParser parser;
    private final Resource lexicalData;
    private final Consumer<Word> wordConsumer;

    public WiktionaryLexicalDataRepository(Consumer<Word> wordConsumer, WiktionaryLexicalDataParser parser, @Value("${latin.data-file}")
    Resource dataFile ) {
        this.parser = parser;
        this.lexicalData = dataFile;
        this.wordConsumer = wordConsumer;
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {
        try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
            parser.parseJsonl(reader, word -> {
                initializeLexicon(word);
            });
        }
    }

    private void initializeLexicon(Word word) {
        wordConsumer.accept(word);

    }

}
