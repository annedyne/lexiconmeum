package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;

import java.util.UUID;

public class TextSearchSuggestionDTO {

    private String word;
    private UUID lexemeId;
    private GrammaticalPosition grammaticalPosition;

    public TextSearchSuggestionDTO(String word, UUID lexemeId, GrammaticalPosition grammaticalPosition){
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

    public void setGrammaticalPosition(GrammaticalPosition grammaticalPosition) {
        this.grammaticalPosition = grammaticalPosition;
    }
}
