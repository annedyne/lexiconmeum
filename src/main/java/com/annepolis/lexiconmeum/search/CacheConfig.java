package com.annepolis.lexiconmeum.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public Lexicon lexicon(){

        return new LexiconImpl(prefixTrie(), suffixTrie(), caffeineCache());
    }

    @Bean
    public Cache<String, List<String>> caffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();
    }


    @Bean
    public Trie prefixTrie(){
        TrieNode root = new TrieNodeImpl();
        return new TrieImpl(root);
    }

    @Bean
    public Trie suffixTrie(){
        TrieNode root = new TrieNodeImpl();
        return new TrieImpl(root);
    }
}
