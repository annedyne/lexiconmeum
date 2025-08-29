package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Configuration
class TextSearchServiceConfig {

    @Bean
    public TextSearchService<String> textSearchService(TextSearchTrieIndexService textSearchTrieIndexService){
        return textSearchTrieIndexService;
    }
    @SuppressWarnings("java:S1452")
    @Bean
    public Consumer<Lexeme> wordConsumer(TextSearchTrieIndexService textSearchTrieIndexService){
        return textSearchTrieIndexService;
    }

    @Bean
    public TextSearchTrieIndexService textSearchTrieCacheComponent(
            @Qualifier("defaultSearchableFormsProvider")
            SearchableFormsProvider searchableFormsProvider

    ) {
        return new TextSearchTrieIndexService(prefixTrie(), suffixTrie(),
                searchableFormsProvider,
                caffeineCache()
        );
    }

    @Bean
    public Cache<String, List<String>> caffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();
    }

    @Bean
    public TextSearchTrieIndex prefixTrie(){
        return new TextSearchTrieIndex(new TextSearchSuggestionMapper());
    }

    @Bean
    public TextSearchTrieIndex suffixTrie(){
        return new TextSearchTrieIndex(new TextSearchSuggestionMapper());
    }


}
