package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.Optional;

public interface SuggestionParentExtractor {

    Optional<String> getSuggestionParent(Lexeme lexeme);

}
