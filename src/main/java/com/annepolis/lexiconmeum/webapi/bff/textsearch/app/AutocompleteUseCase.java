package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import java.util.List;

public interface AutocompleteUseCase {
    List<SuggestionResponse> getWordsStartingWith(String prefix, int limit);
    List<SuggestionResponse> getWordsEndingWith(String suffix, int limit);

}
