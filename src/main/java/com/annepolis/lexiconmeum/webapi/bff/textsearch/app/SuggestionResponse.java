package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.UUID;

public class SuggestionResponse {

    private String word;
    private UUID lexemeId;
    private final PartOfSpeech partOfSpeech;

    public SuggestionResponse(String word, UUID lexemeId, PartOfSpeech partOfSpeech){
        this.word = word;
        this.lexemeId = lexemeId;
        this.partOfSpeech = partOfSpeech;
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

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }
}
