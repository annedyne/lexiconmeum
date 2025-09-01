package com.annepolis.lexiconmeum.webapi.bff.textsearch.index;

import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;

import java.util.List;
import java.util.UUID;

public interface AutocompleteIndexBackend {
    List<FormMatch> searchForMatchingForms(String key, int limit);
    void insert(String form, UUID lexemeId);

}
