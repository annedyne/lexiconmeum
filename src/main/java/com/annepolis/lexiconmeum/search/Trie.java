package com.annepolis.lexiconmeum.search;

import java.util.List;

public interface Trie {

    void insert(String word);
    List<String> search(String prefix, int limit);

}
