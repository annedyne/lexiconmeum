package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * Class that includes specialized parsing for any
 */
public interface PartOfSpeechParser {

    boolean validate(JsonNode root);

    boolean isActive();

    default Optional<Lexeme> parsePartOfSpeech(LexemeBuilder lexemeBuilder, JsonNode jsonNode){
       return Optional.empty();
    }
}
