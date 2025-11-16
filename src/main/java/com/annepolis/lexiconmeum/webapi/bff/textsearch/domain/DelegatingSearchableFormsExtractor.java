package com.annepolis.lexiconmeum.webapi.bff.textsearch.domain;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DelegatingSearchableFormsExtractor implements SearchableFormsExtractor {

    Map<PartOfSpeech, SearchableFormsExtractor> formsProviders;

    public DelegatingSearchableFormsExtractor(Map<PartOfSpeech, SearchableFormsExtractor> formsProviders) {
        this.formsProviders = Map.copyOf(formsProviders);
    }


    @Override
    public Set<String> getSearchableForms(Lexeme lexeme) {
        Objects.requireNonNull(lexeme, "lexeme must not be null");
        PartOfSpeech partOfSpeech =
                Objects.requireNonNull(lexeme.getPartOfSpeech(), "lexeme.partOfSpeech must not be null");

        SearchableFormsExtractor delegate = formsProviders.get(partOfSpeech);
        if (delegate == null) {
            throw new IllegalStateException("No SearchableFormsExtractor configured for partOfSpeech: " + partOfSpeech);
        }

        return delegate.getSearchableForms(lexeme);
    }
}
