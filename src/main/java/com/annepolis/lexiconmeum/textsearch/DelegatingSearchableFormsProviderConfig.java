package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class DelegatingSearchableFormsProviderConfig {
    @Bean
    public DelegatingSearchableFormsProvider defaultSearchableFormsProvider(
            SearchableInflectedFormsProvider inflectedFormsProvider,
            SearchableUninflectedFormsProvider uninflectedFormsProvider
    ){
        Map<GrammaticalPosition, SearchableFormsProvider> formsProviders = new EnumMap<>(GrammaticalPosition.class);
        formsProviders.put(GrammaticalPosition.VERB, inflectedFormsProvider);
        formsProviders.put(GrammaticalPosition.NOUN, inflectedFormsProvider);
        formsProviders.put(GrammaticalPosition.ADJECTIVE, inflectedFormsProvider);
        formsProviders.put(GrammaticalPosition.ADVERB, uninflectedFormsProvider);

        Set<GrammaticalPosition> missing = EnumSet.allOf(GrammaticalPosition.class);
        missing.removeAll(formsProviders.keySet());

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "No SearchableFormsProvider configured for positions: " + missing);
        }

        return new DelegatingSearchableFormsProvider(formsProviders);
    }
}
