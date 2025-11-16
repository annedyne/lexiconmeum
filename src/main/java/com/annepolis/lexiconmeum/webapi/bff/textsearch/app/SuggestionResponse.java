package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.UUID;

public class SuggestionResponse {
    private final String word;
    private final UUID lexemeId;
    private final PartOfSpeech partOfSpeech;

    // Form that most obviously indicates the Lexeme parent
    private final String suggestionParent;

    public SuggestionResponse(String word, UUID lexemeId, PartOfSpeech partOfSpeech, String suggestionParent){
        this.word = word;
        this.lexemeId = lexemeId;
        this.partOfSpeech = partOfSpeech;
        this.suggestionParent = suggestionParent;
    }
    public String getWord() { return word;}

    public UUID getLexemeId() {
        return lexemeId;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getSuggestionParent() {
        return suggestionParent;
    }
}
