package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.springframework.stereotype.Component;

@Component
public class POSNonInflectedFormParser implements PartOfSpeechParser {
    @Override
    public boolean isActive() {
        return false;
    }

    // PLACEHOLDER UNTIL NOUN PARSING LOGIC TRANSFERRED FROM MAIN PARSER

}
