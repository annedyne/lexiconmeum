package com.annepolis.lexiconmeum.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


@SpringBootTest(properties = "cache.mock=false")
@ContextConfiguration(classes = LexiconImplTest.TestConfig.class)
public class LexiconImplTest {

    private Cache<String, List<String>> cache;

    @Configuration
    static class TestConfig {
        @Bean
        public Cache<String, List<String>> caffeineCache(@Value("${cache.mock:false}") boolean useMock) {
            return useMock ? mock(Cache.class) : Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .expireAfterWrite(Duration.ofMinutes(30))
                    .build();
        }
    }


    @Test
    void getWordsStartingWithReturnsCachedResults(){
        Trie prefixTrie = new TrieImpl(new TrieNodeImpl());
        Trie suffixTrie = new TrieImpl(new TrieNodeImpl());

        List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");

        Lexicon lexicon = new LexiconImpl(prefixTrie, suffixTrie, inputs, cache);

        List<String> result = lexicon.getWordsStartingWith("am", 10);
        assertEquals(6, result.size());

        result = lexicon.getWordsStartingWith("ami", 10);
        assertEquals(1, result.size());

    }

    @Test
    void getWordsEndingWithReturnsCachedResults(){
        Trie prefixTrie = new TrieImpl(new TrieNodeImpl());
        Trie suffixTrie = new TrieImpl(new TrieNodeImpl());

        List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");

        Lexicon lexicon = new LexiconImpl(prefixTrie, suffixTrie, inputs, cache);

        List<String> result = lexicon.getWordsEndingWith("us", 10);

        assertEquals(1, result.size());

    }

}
