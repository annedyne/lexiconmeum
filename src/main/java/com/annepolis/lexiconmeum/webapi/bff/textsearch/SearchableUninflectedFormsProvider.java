package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SearchableUninflectedFormsProvider implements SearchableFormsProvider {

    @Override
    public Set<String> getSearchableForms(Lexeme lexeme) {
        String lemma = lexeme.getLemma();
        if (lemma == null || lemma.isBlank()) {
            return Set.of();
        }
        return Set.of(lemma);
    }
}
