package com.annepolis.lexiconmeum.search;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LexiconImpl implements Lexicon {

    private final Trie prefixTrie;
    private final Trie suffixTrie;
    private final Cache<String, List<String>> cache;

    public LexiconImpl(Trie prefixTrie, Trie suffixTrie, Cache<String, List<String>> cache){
        this.prefixTrie = prefixTrie;
        this.suffixTrie = suffixTrie;
        this.cache = cache;
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
