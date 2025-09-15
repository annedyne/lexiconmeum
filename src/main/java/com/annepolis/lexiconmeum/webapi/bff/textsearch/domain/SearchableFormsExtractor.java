package com.annepolis.lexiconmeum.webapi.bff.textsearch.domain;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.Set;

public interface SearchableFormsExtractor {
    Set<String> getSearchableForms(Lexeme lexeme);
}
