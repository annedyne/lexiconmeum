package com.annepolis.lexiconmeum.webapi.bff.textsearch.domain;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class DelegatingSearchableFormsExtractorConfig {
    @Bean
    public DelegatingSearchableFormsExtractor defaultSearchableFormsExtractor(
            SearchableInflectedFormsExtractor inflectedFormsProvider,
            SearchableUninflectedFormsExtractor uninflectedFormsProvider
    ){
        Map<GrammaticalPosition, SearchableFormsExtractor> formsExtractors = new EnumMap<>(GrammaticalPosition.class);
        formsExtractors.put(GrammaticalPosition.VERB, inflectedFormsProvider);
        formsExtractors.put(GrammaticalPosition.NOUN, inflectedFormsProvider);
        formsExtractors.put(GrammaticalPosition.ADJECTIVE, inflectedFormsProvider);
        formsExtractors.put(GrammaticalPosition.ADVERB, uninflectedFormsProvider);

        Set<GrammaticalPosition> missing = EnumSet.allOf(GrammaticalPosition.class);
        missing.removeAll(formsExtractors.keySet());

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "No SearchableFormsExtractor configured for positions: " + missing);
        }

        return new DelegatingSearchableFormsExtractor(formsExtractors);
    }
}
