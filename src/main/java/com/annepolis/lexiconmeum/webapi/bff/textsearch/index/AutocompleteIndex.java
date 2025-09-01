package com.annepolis.lexiconmeum.webapi.bff.textsearch.index;

import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;

import java.util.List;

public interface AutocompleteIndex {

    List<FormMatch> matchByPrefix(String prefix, int limit);
    List<FormMatch> matchBySuffix(String suffix, int limit);

}
