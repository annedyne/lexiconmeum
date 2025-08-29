package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextSearchSuggestionMapper {


    TextSearchSuggestionDTO toTextSearchDTO(String word, UUID lexemeId, GrammaticalPosition grammaticalPosition){
        return new TextSearchSuggestionDTO(word, lexemeId, grammaticalPosition);
    }

    String toFormIdString(String prefix, UUID lexemeKey){
        return prefix + ": " + lexemeKey;
    }


}
