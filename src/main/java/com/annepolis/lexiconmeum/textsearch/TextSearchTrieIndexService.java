package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.List;

class TextSearchTrieIndexService implements TextSearchService<String>, LexemeSink {

    private final TextSearchTrieIndex prefixTextSearchIndex;
    private final TextSearchTrieIndex suffixTextSearchIndex;
    private final SearchableFormsProvider searchableFormsProvider;
    private final Cache<String, List<String>> cache;

    public TextSearchTrieIndexService(TextSearchTrieIndex prefixTextSearchIndex, TextSearchTrieIndex suffixTextSearchIndex, SearchableFormsProvider searchableFormsProvider, Cache<String, List<String>> cache){
        this.prefixTextSearchIndex = prefixTextSearchIndex;
        this.suffixTextSearchIndex = suffixTextSearchIndex;
        this.searchableFormsProvider = searchableFormsProvider;
        this.cache = cache;
    }

    @Override
    public List<String> getWordsStartingWith(String prefix, int limit) {
        return cache.get(prefix, k -> prefixTextSearchIndex.searchForMatchingForms(prefix, limit));
    }

    @Override
    public List<String> getWordsEndingWith(String suffix, int limit) {
        return cache.get("_" + suffix, k -> suffixTextSearchIndex.searchForMatchingForms(suffix, limit));
    }

    public void populateIndex(Lexeme lexeme) {
        for(String form: searchableFormsProvider.getSearchableForms(lexeme)){
            prefixTextSearchIndex.insert(form, lexeme.getId());
            suffixTextSearchIndex.insert(new StringBuilder(form).reverse().toString(), lexeme.getId());
        }
    }

    @Override
    public void accept(Lexeme lexeme) {
        populateIndex(lexeme);
    }

}
