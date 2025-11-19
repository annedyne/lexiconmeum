package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.index.AutocompleteIndex;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AutocompleteService implements AutocompleteUseCase {

    private final AutocompleteIndex index;
    private final SuggestionMapper suggestionMapper;
    private final LexemeReader lexemeReader;
    private final SuggestionParentExtractor suggestionParentExtractor;

    public AutocompleteService(AutocompleteIndex index,
                               SuggestionMapper suggestionMapper,
                               LexemeReader lexemeReader,
                               @Qualifier("defaultSuggestionParentExtractor") SuggestionParentExtractor suggestionParentExtractor) {

       this.index = index;
       this.suggestionMapper = suggestionMapper;
       this.lexemeReader = lexemeReader;
        this.suggestionParentExtractor = suggestionParentExtractor;
    }

    @Override
    public List<SuggestionResponse> getWordsStartingWith(String prefix, int limit) {
        List<FormMatch> matches = index.matchByPrefix(prefix, limit);
        return mapMatchesToSuggestionResponses(matches);
    }

    @Override
    public List<SuggestionResponse> getWordsEndingWith(String suffix, int limit) {
        List<FormMatch> matches = index.matchBySuffix(suffix, limit);

        return mapMatchesToSuggestionResponses(matches);
    }

    List<SuggestionResponse> mapMatchesToSuggestionResponses(List<FormMatch> matches) {
        return matches.stream()
                .filter(match -> match != null
                        && match.form() != null
                        && !match.form().isBlank()
                        && match.lexemeId() != null)
                .sorted(Comparator.comparing(match -> match.form().toLowerCase()))
                .map(match -> lexemeReader.getLexemeIfPresent(match.lexemeId())
                        .map(lexeme ->
                            suggestionMapper.toResponse(match.form(), match.lexemeId(), lexeme.getPartOfSpeech(),
                                    suggestionParentExtractor.getSuggestionParent(lexeme))
                        ))
                .flatMap(Optional::stream)
                .toList();
    }
}
