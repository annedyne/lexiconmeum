package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VerbSuggestionParentExtractor implements SuggestionParentExtractor {

    private final InflectionKey inflectionKey;
    public VerbSuggestionParentExtractor(InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
    }

    @Override
    public Optional<String> getSuggestionParent(Lexeme lexeme) {
        String key = inflectionKey.buildSecondPrincipalPartKey();
        return Optional.ofNullable(lexeme.getInflectionIndex().get(key))
                .map(Inflection::getForm);
    }
}
