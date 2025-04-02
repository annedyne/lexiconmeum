package com.annepolis.lexiconmeum.search;

import java.util.List;

public interface Lexicon {

    List<String> getWordsStartingWith(String prefix, int limit);
    List<String> getWordsEndingWith(String suffix, int limit);
}
