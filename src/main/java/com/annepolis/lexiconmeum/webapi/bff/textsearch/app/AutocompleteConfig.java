package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.index.AutocompleteIndex;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutocompleteConfig {

    @Bean
    public AutocompleteUseCase autocompleteUseCase(
            AutocompleteIndex index,
            SuggestionMapper mapper,
            LexemeReader lexemeReader,
            @Qualifier("defaultSuggestionParentExtractor")SuggestionParentExtractor suggestionParentExtractor) {
        return new AutocompleteService(index, mapper, lexemeReader, suggestionParentExtractor);
    }

}
