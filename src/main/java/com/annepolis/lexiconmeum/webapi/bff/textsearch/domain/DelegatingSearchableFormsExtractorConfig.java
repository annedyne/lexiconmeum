package com.annepolis.lexiconmeum.webapi.bff.textsearch.domain;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
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
        Map<PartOfSpeech, SearchableFormsExtractor> formsExtractors = new EnumMap<>(PartOfSpeech.class);
        formsExtractors.put(PartOfSpeech.VERB, inflectedFormsProvider);
        formsExtractors.put(PartOfSpeech.NOUN, inflectedFormsProvider);
        formsExtractors.put(PartOfSpeech.ADJECTIVE, inflectedFormsProvider);
        formsExtractors.put(PartOfSpeech.ADVERB, uninflectedFormsProvider);
        formsExtractors.put(PartOfSpeech.PREPOSITION, uninflectedFormsProvider);
        formsExtractors.put(PartOfSpeech.POSTPOSITION, uninflectedFormsProvider);

        Set<PartOfSpeech> missing = EnumSet.allOf(PartOfSpeech.class);
        missing.removeAll(formsExtractors.keySet());

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "No SearchableFormsExtractor configured for partsOfSpeech: " + missing);
        }

        return new DelegatingSearchableFormsExtractor(formsExtractors);
    }
}
