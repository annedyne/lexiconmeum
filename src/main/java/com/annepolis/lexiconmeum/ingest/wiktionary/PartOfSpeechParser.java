package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class that includes specialized parsing for any
 */
public interface PartOfSpeechParser {

    default ParsedResultProcessor parsePartOfSpeech(JsonNode root){
        return ParsedResultProcessor.EMPTY;
    }

    boolean isActive();
}
