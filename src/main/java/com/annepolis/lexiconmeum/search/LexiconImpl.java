package com.annepolis.lexiconmeum.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;

public class LexiconImpl implements Lexicon {

    private final Trie prefixTrie;
    private final Trie suffixTrie;
    private Cache<String, List<String>> cache;

    public LexiconImpl(Trie prefixTrie, Trie suffixTrie, List<String> words, Cache<String, List<String>> cache){
        this.prefixTrie = prefixTrie;
        this.suffixTrie = suffixTrie;
        this.cache = cache;
        initCache(words);
    }

    private void initCache(List<String> words){
        cache = Caffeine.newBuilder()
                .maximumSize(10_000)  // Cache up to 10,000 queries
                .expireAfterWrite(java.time.Duration.ofMinutes(30))
                .build();

        for (String word : words) {
            prefixTrie.insert(word);
            suffixTrie.insert(new StringBuilder(word).reverse().toString());
        }
    }

    public List<String> getWordsStartingWith(String prefix, int limit) {
        return cache.get(prefix, k -> prefixTrie.search(prefix, limit));
    }

    public List<String> getWordsEndingWith(String suffix, int limit) {
        String reversedSuffix = new StringBuilder(suffix).reverse().toString();

        List<String> results =  cache.get("_" + suffix, k -> suffixTrie.search(reversedSuffix, limit));
        return results.stream()
                .map(s -> new StringBuilder(s).reverse().toString())
                .toList();
    }
}
