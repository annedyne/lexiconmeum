package com.annepolis.lexiconmeum.textsearch;

import java.util.List;

public interface TextSearchIndex {

    List<String> searchForMatchingForms(String prefix, int limit);
}
