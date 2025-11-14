package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Component;

@Component
public class CommonSuggestionParentExtractor implements SuggestionParentExtractor {

    public String getSuggestionParent(Lexeme lexeme){
        return lexeme.getCanonicalForm();
    }
}
