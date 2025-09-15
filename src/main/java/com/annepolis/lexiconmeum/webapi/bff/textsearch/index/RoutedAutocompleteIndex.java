package com.annepolis.lexiconmeum.webapi.bff.textsearch.index;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.SearchableFormsExtractor;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class RoutedAutocompleteIndex implements AutocompleteIndex, LexemeSink {

    private final AutocompleteIndexBackend prefixBackend;
    private final AutocompleteIndexBackend suffixBackend;

    private final SearchableFormsExtractor formsExtractor;
    private final Cache<String, List<FormMatch>> cache;

    public RoutedAutocompleteIndex(
            AutocompleteIndexBackend prefixBackend,
            AutocompleteIndexBackend suffixBackend,
            SearchableFormsExtractor formsExtractor,
            Cache<String, List<FormMatch>> cache
    ){
        this.prefixBackend = prefixBackend;
        this.suffixBackend = suffixBackend;
        this.formsExtractor = formsExtractor;
        this.cache = cache;
    }

    @Override
    public List<FormMatch> matchByPrefix(String prefix, int limit) {
        String key = "P|" + prefix;
        return cache.get(key, k -> prefixBackend.searchForMatchingForms(prefix, limit));
    }

    @Override
    public List<FormMatch> matchBySuffix(String suffix, int limit) {
        String reversed = new StringBuilder(suffix).reverse().toString();
        String key = "S|" + reversed;
        return cache.get(key, k -> suffixBackend.searchForMatchingForms(reversed, limit)
                .stream()
                .filter(m -> m != null && m.form() != null && !m.form().isBlank())
                .map(m -> new FormMatch(new StringBuilder(m.form()).reverse().toString(), m.lexemeId()))
                .toList()
        );

    }

    /**
     * Populate both backends with all searchable forms:
     */
    @Override
    public void accept(Lexeme lexeme) {
        if (lexeme == null) return;

        UUID id = lexeme.getId();
        if (id == null) return;

        var forms = new ArrayList<>(formsExtractor.getSearchableForms(lexeme));
        for (String form : forms) {
            if (form == null || form.isBlank()) continue;
            prefixBackend.insert(form, id);
            suffixBackend.insert(new StringBuilder(form).reverse().toString(), id);
        }
    }

}
