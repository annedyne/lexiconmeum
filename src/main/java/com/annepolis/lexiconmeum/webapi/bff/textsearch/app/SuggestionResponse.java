package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;

import java.util.UUID;

public class SuggestionResponse {

    private String word;
    private UUID lexemeId;
    private GrammaticalPosition grammaticalPosition;

    public SuggestionResponse(String word, UUID lexemeId, GrammaticalPosition grammaticalPosition){
        this.word = word;
        this.lexemeId = lexemeId;
        this.grammaticalPosition = grammaticalPosition;
    }
    public String getWord() { return word;}

    public void setWord(String word) {
        this.word = word;
    }

    public UUID getLexemeId() {
        return lexemeId;
    }

    public void setLexemeId(UUID lexemeId) {
        this.lexemeId = lexemeId;
    }

    public GrammaticalPosition getGrammaticalPosition() {
        return grammaticalPosition;
    }
}
