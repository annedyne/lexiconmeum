package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SuggestionMapper {

    SuggestionResponse toResponse(String word, UUID lexemeId, GrammaticalPosition grammaticalPosition){
        return new SuggestionResponse(word, lexemeId, grammaticalPosition);
    }
}
