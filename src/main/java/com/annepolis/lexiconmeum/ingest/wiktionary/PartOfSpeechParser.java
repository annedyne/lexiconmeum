package com.annepolis.lexiconmeum.ingest.wiktionary;

import tools.jackson.databind.JsonNode;

/**
 * Class that includes specialized parsing for any
 */
public interface PartOfSpeechParser {

    default ParsedResultProcessor parsePartOfSpeech(JsonNode root, POSParserKey parserKey){
        return ParsedResultProcessor.EMPTY;
    }
}
