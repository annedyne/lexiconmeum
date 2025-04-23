package com.annepolis.lexiconmeum.search;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrieNodeImpl implements TrieNode {

    protected TrieNode parent;
    private Character content;
    protected Map<Character, TrieNode> children = new HashMap<>();
     // Marks whether this node represents the end of a complete word
    boolean isEndOfWord;

    @Override
    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    @Override
    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    @Override
    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    @Override
    public void setParent(TrieNode parent) {
        this.parent = parent;
    }

    @Override
    public Character getContent() {
        return content;
    }

    @Override
    public void setContent(Character content) {
        this.content = content;
    }
}
