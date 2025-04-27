package com.annepolis.lexiconmeum.textsearch;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.List;
import java.util.function.Consumer;


class TextSearchTrieCacheComponent implements TextSearchComponent, Consumer<Word> {

    private final TextSearchIndex prefixTextSearchIndex;
    private final TextSearchIndex suffixTextSearchIndex;
    private final Cache<String, List<String>> cache;

    public TextSearchTrieCacheComponent(TextSearchIndex prefixTextSearchIndex, TextSearchIndex suffixTextSearchIndex, Cache<String, List<String>> cache){
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

    public void populateCache(Word word) {
        for(Inflection inflection: word.getInflections()){
            prefixTextSearchIndex.insert(inflection.toString());
            suffixTextSearchIndex.insert(new StringBuilder(inflection.toString()).reverse().toString());
        }
    }

    @Override
    public void accept(Word word) {
        populateCache(word);
    }

}
