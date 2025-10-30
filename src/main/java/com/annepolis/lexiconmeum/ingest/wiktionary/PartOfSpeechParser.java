package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.fasterxml.jackson.databind.JsonNode;

public interface PartOfSpeechParser {

    boolean validate(JsonNode root);

    default void addInflections(JsonNode formsNode, LexemeBuilder builder){
        //  Do nothing by default
    }
}
