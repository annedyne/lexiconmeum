package com.annepolis.lexiconmeum.webapi.bff.textsearch.index;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.SearchableFormsExtractor;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Configuration
class TrieAutocompleteIndexConfig {

    @Bean
    public Cache<String, List<FormMatch>> autocompleteCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(Duration.ofDays(15))
                .build();
    }

    // Backends (not exposed as AutocompleteIndex)
    @Bean
    TrieAutocompleteIndex prefixTrie() {
        return new TrieAutocompleteIndex();
    }

    @Bean
    TrieAutocompleteIndex suffixTrie() {
        return new TrieAutocompleteIndex();
    }

    // Single bean: package-private class, but public bean method is fine
    @Bean
    RoutedAutocompleteIndex routedAutocompleteIndex(TrieAutocompleteIndex prefixTrie,
                                                    TrieAutocompleteIndex suffixTrie,
                                                    @Qualifier("defaultSearchableFormsExtractor") SearchableFormsExtractor formsExtractor,
                                                    Cache<String, List<FormMatch>> autocompleteCache) {
        return new RoutedAutocompleteIndex(prefixTrie, suffixTrie, formsExtractor, autocompleteCache);
    }

    // Expose the same instance as a Consumer<Lexeme> (LexemeSink)
    @SuppressWarnings("java:S1452")
    @Bean
    Consumer<Lexeme> wordConsumer(RoutedAutocompleteIndex routedAutocompleteIndex) {
        return routedAutocompleteIndex;
    }

}
