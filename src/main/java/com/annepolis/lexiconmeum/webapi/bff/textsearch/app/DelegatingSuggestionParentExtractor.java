package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.Map;
import java.util.Objects;

public class DelegatingSuggestionParentExtractor implements SuggestionParentExtractor {

    private final Map<PartOfSpeech, SuggestionParentExtractor> suggestionParentExtractors;

    public DelegatingSuggestionParentExtractor(Map<PartOfSpeech, SuggestionParentExtractor> suggestionParentExtractorMap){
        this.suggestionParentExtractors = Map.copyOf(suggestionParentExtractorMap);
    }
    @Override
    public String getSuggestionParent(Lexeme lexeme) {
        Objects.requireNonNull(lexeme, "lexeme must not be null");
        PartOfSpeech partOfSpeech =
                Objects.requireNonNull(lexeme.getPartOfSpeech(), "lexeme.partOfSpeech must not be null");

        SuggestionParentExtractor delegate = suggestionParentExtractors.get(lexeme.getPartOfSpeech());
        if (delegate == null) {
            throw new IllegalStateException("No SuggestionParentExtractor configured for partOfSpeech: " + partOfSpeech);
        }
        return delegate.getSuggestionParent(lexeme);
    }
}
