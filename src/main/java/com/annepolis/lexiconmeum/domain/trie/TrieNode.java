package com.annepolis.lexiconmeum.domain.trie;

import java.util.Map;

public interface TrieNode {

    Map<Character, TrieNode> getChildren();

    boolean isEndOfWord();

    void setEndOfWord(boolean endOfWord);

    void setParent(TrieNode parent);

    @Override
    String toString();

    Character getContent();

    void setContent(Character content);

}
