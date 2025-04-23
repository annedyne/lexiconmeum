package com.annepolis.lexiconmeum.search;

import java.util.List;

public interface Trie {

    void insert(String word);

    void insert(List<String> words);

    List<String> search(String prefix, int limit);

}
