package com.annepolis.lexiconmeum.domain.lexicon;

import java.util.List;

public interface Lexicon {

    List<String> getWordsStartingWith(String prefix, int limit);
    List<String> getWordsEndingWith(String suffix, int limit);

    void acceptWord(String word);
}
