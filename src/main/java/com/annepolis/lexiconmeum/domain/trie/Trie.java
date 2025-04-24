package com.annepolis.lexiconmeum.domain.trie;

import java.util.List;

public interface Trie {

    void insert(String word);

    void insert(List<String> words);

    List<String> search(String prefix, int limit);

}
