package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.index.AutocompleteIndex;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutocompleteService implements AutocompleteUseCase {

    private final AutocompleteIndex index;
    private final SuggestionMapper suggestionMapper;
    private final LexemeReader lexemeReader;

    public AutocompleteService(AutocompleteIndex index,
                               SuggestionMapper suggestionMapper,
                               LexemeReader lexemeReader) {

       this.index = index;
       this.suggestionMapper = suggestionMapper;
       this.lexemeReader = lexemeReader;
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
                .filter(match -> match != null && match.form() != null && !match.form().isBlank() && match.lexemeId() != null)
                .map(match -> lexemeReader.getLexemeIfPresent(match.lexemeId())
                        .map(lexeme ->
                                suggestionMapper.toResponse(match.form(), match.lexemeId(), lexeme.getGrammaticalPosition())
                        ))
                .flatMap(Optional::stream)
                .toList();
    }
}
