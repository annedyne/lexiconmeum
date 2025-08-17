package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.Set;

public interface SearchableFormsProvider {
    Set<String> getSearchableForms(Lexeme lexeme);
}
