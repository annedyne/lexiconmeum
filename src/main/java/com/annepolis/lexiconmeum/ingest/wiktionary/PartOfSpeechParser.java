package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public interface PartOfSpeechParser {

    boolean validate(JsonNode root);

    default Optional<Lexeme> parsePartOfSpeech(LexemeBuilder lexemeBuilder, JsonNode jsonNode){
       return Optional.empty();
    }

    default void addInflections(LexemeBuilder lexemeBuilder, JsonNode formsNode){
        // do nothing by default
    }
}
