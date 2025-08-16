package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.List;

class TextSearchTrieIndexService implements TextSearchService<String>, LexemeSink {

    private final TextSearchTrieIndex prefixTextSearchIndex;
    private final TextSearchTrieIndex suffixTextSearchIndex;
    private final Cache<String, List<String>> cache;

    public TextSearchTrieIndexService(TextSearchTrieIndex prefixTextSearchIndex, TextSearchTrieIndex suffixTextSearchIndex, Cache<String, List<String>> cache){
        this.prefixTextSearchIndex = prefixTextSearchIndex;
        this.suffixTextSearchIndex = suffixTextSearchIndex;
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
        for(Inflection inflection: lexeme.getInflections()){
            prefixTextSearchIndex.insert(inflection.getForm(), lexeme.getId());
            suffixTextSearchIndex.insert(new StringBuilder(inflection.getForm()).reverse().toString(), lexeme.getId());
        }
    }

    @Override
    public void accept(Lexeme lexeme) {
        populateIndex(lexeme);
    }

}
