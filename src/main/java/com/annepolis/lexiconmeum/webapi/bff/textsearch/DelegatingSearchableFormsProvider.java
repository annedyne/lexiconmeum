package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DelegatingSearchableFormsProvider implements SearchableFormsProvider {

    Map<GrammaticalPosition, SearchableFormsProvider> formsProviders;

    public DelegatingSearchableFormsProvider(Map<GrammaticalPosition, SearchableFormsProvider> formsProviders) {
        this.formsProviders = Map.copyOf(formsProviders);
    }


    @Override
    public Set<String> getSearchableForms(Lexeme lexeme) {
        Objects.requireNonNull(lexeme, "lexeme must not be null");
        GrammaticalPosition position =
                Objects.requireNonNull(lexeme.getGrammaticalPosition(), "lexeme.grammaticalPosition must not be null");

        SearchableFormsProvider delegate = formsProviders.get(position);
        if (delegate == null) {
            throw new IllegalStateException("No SearchableFormsProvider configured for position: " + position);
        }

        return formsProviders.get(lexeme.getGrammaticalPosition()).getSearchableForms(lexeme);
    }
}
