package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class DelegatingSuggestionParentExtractorConfig {

    @Bean
    public DelegatingSuggestionParentExtractor defaultSuggestionParentExtractor(
            CommonSuggestionParentExtractor commonSuggestionParentExtractor,
            VerbSuggestionParentExtractor verbSuggestionParentExtractor
    ){
        Map<PartOfSpeech, SuggestionParentExtractor> suggestionParentExtractors = new EnumMap<>(PartOfSpeech.class);

        suggestionParentExtractors.put(PartOfSpeech.ADJECTIVE, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.ADVERB, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.CONJUNCTION, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.DETERMINER, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.NOUN, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.PREPOSITION, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.POSTPOSITION, commonSuggestionParentExtractor);
        suggestionParentExtractors.put(PartOfSpeech.PRONOUN, commonSuggestionParentExtractor);

        suggestionParentExtractors.put(PartOfSpeech.VERB, verbSuggestionParentExtractor);

        Set<PartOfSpeech> missing = EnumSet.allOf(PartOfSpeech.class);
        missing.removeAll(suggestionParentExtractors.keySet());

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "No SuggestionParentExtractors configured for partsOfSpeech: " + missing);
        }

        return new DelegatingSuggestionParentExtractor(suggestionParentExtractors);

    }
}
