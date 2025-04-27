package com.annepolis.lexiconmeum.textsearch;

import java.util.List;

public interface TextSearchComponent {

    List<String> getWordsStartingWith(String prefix, int limit);
    List<String> getWordsEndingWith(String suffix, int limit);
}
