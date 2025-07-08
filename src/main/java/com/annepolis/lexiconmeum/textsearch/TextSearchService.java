package com.annepolis.lexiconmeum.textsearch;

import java.util.List;

public interface TextSearchService<T> {

    List<T> getWordsStartingWith(String prefix, int limit);
    List<T> getWordsEndingWith(String suffix, int limit);


}
