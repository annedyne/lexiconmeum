package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import java.util.List;

public interface TextSearchIndex {

    List<String> searchForMatchingForms(String prefix, int limit);
}
