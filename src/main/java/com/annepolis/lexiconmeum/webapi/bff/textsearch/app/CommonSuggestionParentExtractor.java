package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommonSuggestionParentExtractor implements SuggestionParentExtractor {

    @Override
    public Optional<String> getSuggestionParent(Lexeme lexeme){
        return Optional.ofNullable(lexeme.getCanonicalForm());
    }
}
