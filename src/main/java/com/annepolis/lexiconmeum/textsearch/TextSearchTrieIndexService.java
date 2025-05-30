package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeSink;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.List;

class TextSearchTrieIndexService implements TextSearchService, LexemeSink {

    private final TextSearchIndex prefixTextSearchIndex;
    private final TextSearchIndex suffixTextSearchIndex;
    private final Cache<String, List<String>> cache;

    public TextSearchTrieIndexService(TextSearchIndex prefixTextSearchIndex, TextSearchIndex suffixTextSearchIndex, Cache<String, List<String>> cache){
        this.prefixTextSearchIndex = prefixTextSearchIndex;
        this.suffixTextSearchIndex = suffixTextSearchIndex;
        this.cache = cache;
    }

    public List<String> getWordsStartingWith(String prefix, int limit) {
        return cache.get(prefix, k -> prefixTextSearchIndex.search(prefix, limit));
    }

    public List<String> getWordsEndingWith(String suffix, int limit) {
        String reversedSuffix = new StringBuilder(suffix).reverse().toString();

        List<String> results =  cache.get("_" + suffix, k -> suffixTextSearchIndex.search(reversedSuffix, limit));
        return results.stream()
                .map(s -> new StringBuilder(s).reverse().toString())
                .toList();
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
