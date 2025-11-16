package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.springframework.stereotype.Component;

@Component
public class VerbSuggestionParentExtractor implements SuggestionParentExtractor {

    private final InflectionKey inflectionKey;
    public VerbSuggestionParentExtractor(InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
    }

    @Override
    public String getSuggestionParent(Lexeme lexeme) {
        String key = inflectionKey.buildSecondPrincipalPartKey();
        return lexeme.getInflectionIndex().get(key).getForm();
    }
}
