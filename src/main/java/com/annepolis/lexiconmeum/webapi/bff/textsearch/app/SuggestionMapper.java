package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SuggestionMapper {

    SuggestionResponse toResponse(String word, UUID lexemeId, PartOfSpeech partOfSpeech){
        return new SuggestionResponse(word, lexemeId, partOfSpeech);
    }
}
