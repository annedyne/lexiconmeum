package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import java.util.List;

public interface TextSearchService<T> {

    List<T> getWordsStartingWith(String prefix, int limit);
    List<T> getWordsEndingWith(String suffix, int limit);


}
