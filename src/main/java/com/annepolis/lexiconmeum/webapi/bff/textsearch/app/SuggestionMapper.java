package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SuggestionMapper {

    // find all matches
    // get Lexemes
    // only return Lexeme and don't repeat so put in map
    SuggestionResponse toResponse(String word, UUID lexemeId, PartOfSpeech partOfSpeech, String displayParent){
        return new SuggestionResponse(word, lexemeId, partOfSpeech, displayParent);
    }
}
