package com.annepolis.lexiconmeum.service;

import com.annepolis.lexiconmeum.data.WiktionaryParser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Service
public class LexicalDataLoader {

    private final WiktionaryParser parser;
    private final Resource lexicalData;

    public LexicalDataLoader(WiktionaryParser parser, @Value("${latin.data-file}")
    Resource dataFile ) {
        this.parser = parser;
        this.lexicalData = dataFile;
    }

    @PostConstruct
    public void loadDataOnStartup() throws IOException {
        try (Reader reader = new InputStreamReader(lexicalData.getInputStream())) {
            parser.parseJsonl(reader);
        }

    }

}
