package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Configuration
class TextSearchServiceConfig {

    @Bean
    public TextSearchService textSearchService(TextSearchTrieIndexService textSearchTrieIndexService){
        return textSearchTrieIndexService;
    }
    @Bean
    public Consumer<Lexeme> wordConsumer(TextSearchTrieIndexService textSearchTrieIndexService){
        return textSearchTrieIndexService;
    }

    @Bean
    public TextSearchTrieIndexService textSearchTrieCacheComponent() {
        return new TextSearchTrieIndexService(prefixTrie(), suffixTrie(), caffeineCache());
    }

    @Bean
    public Cache<String, List<String>> caffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();
    }

    @Bean
    public TextSearchIndex prefixTrie(){
        return new TextSearchIndex();
    }

    @Bean
    public TextSearchIndex suffixTrie(){
        return new TextSearchIndex();
    }

}
