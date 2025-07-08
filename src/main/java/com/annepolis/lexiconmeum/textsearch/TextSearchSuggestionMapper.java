package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
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
