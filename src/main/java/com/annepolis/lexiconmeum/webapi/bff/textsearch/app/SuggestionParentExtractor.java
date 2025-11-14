package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

public interface SuggestionParentExtractor {

    String getSuggestionParent(Lexeme lexeme);

}
