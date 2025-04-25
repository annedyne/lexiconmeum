package com.annepolis.lexiconmeum.config;

import com.annepolis.lexiconmeum.domain.lexicon.InMemoryLexicon;
import com.annepolis.lexiconmeum.domain.lexicon.Lexicon;
import com.annepolis.lexiconmeum.domain.trie.BasicTrieNode;
import com.annepolis.lexiconmeum.domain.trie.ReversibleTrie;
import com.annepolis.lexiconmeum.domain.trie.Trie;
import com.annepolis.lexiconmeum.domain.trie.TrieNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class LexiconConfig {

    @Bean
    public Lexicon lexicon(){
        return new InMemoryLexicon(prefixTrie(), suffixTrie(), caffeineCache());
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
        TrieNode root = new BasicTrieNode();
        return new ReversibleTrie(root);
    }

    @Bean
    public Trie suffixTrie(){
        TrieNode root = new BasicTrieNode();
        return new ReversibleTrie(root);
    }

}
