package com.annepolis.lexiconmeum.bootstrap;

import com.annepolis.lexiconmeum.data.WiktionaryParser;
import com.annepolis.lexiconmeum.domain.lexicon.Lexicon;
import com.annepolis.lexiconmeum.domain.model.Inflection;
import com.annepolis.lexiconmeum.domain.model.Word;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class LexiconInitializer {

    private final WiktionaryParser parser;
    private final Resource lexicalData;
    private final Lexicon lexicon;

    public LexiconInitializer(Lexicon lexicon, WiktionaryParser parser, @Value("${latin.data-file}")
    Resource dataFile ) {
        this.parser = parser;
        this.lexicalData = dataFile;
        this.lexicon = lexicon;
    }

    @PostConstruct
    private void loadLexicalData() throws IOException {
        try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
            for (Word word : parser.parseJsonl(reader)) {
                initializeLexicon(word);
            }
        }
    }

    private void initializeLexicon(Word word) {
        for(Inflection inflection : word.getInflections()){
            lexicon.acceptWord(inflection.getInflection());
        }
    }

}
