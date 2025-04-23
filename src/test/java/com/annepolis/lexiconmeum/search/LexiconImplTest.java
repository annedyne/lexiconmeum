package com.annepolis.lexiconmeum.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LexiconImplTest {

    @Autowired
    Lexicon testLexicon;

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        public Lexicon testLexicon(){
            return new LexiconImpl(testPrefixTrie(), testSuffixTrie(), testCaffeineCache());
        }

        @Bean
        @Primary
        public Cache<String, List<String>> testCaffeineCache() {
            return Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .expireAfterWrite(Duration.ofMinutes(30))
                    .build();
        }

        @Bean
        public Trie testPrefixTrie(){
            TrieNode root = new TrieNodeImpl();
            Trie trie = new TrieImpl(root);
            List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");

            trie.insert(inputs);
            return trie;
        }

        @Bean
        public Trie testSuffixTrie(){
            TrieNode root = new TrieNodeImpl();
            Trie trie = new TrieImpl(root);
            List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");
            trie.insert(inputs.stream().map( word -> new StringBuilder(word).reverse().toString()).toList());
            return trie;
        }

    }


    @Test
    void getWordsStartingWithReturnsExpectedResultsFromCache(){

        List<String> result = testLexicon.getWordsStartingWith("am", 10);
        assertEquals(6, result.size());

        result = testLexicon.getWordsStartingWith("ami", 10);
        assertEquals(1, result.size());

    }

    @Test
    void getWordsEndingWithReturnsCachedResults(){
        List<String> result = testLexicon.getWordsEndingWith("us", 10);

        assertEquals(1, result.size());

    }

}
