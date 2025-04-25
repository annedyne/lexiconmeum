package com.annepolis.lexiconmeum.domain.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasicTrieNodeTest {

    @Test
    void constructorTest(){
        BasicTrieNode trieNode = new BasicTrieNode();
        Assertions.assertFalse(trieNode.isEndOfWord);

        assertNotNull(trieNode.children);
    }

    @Test
    void setIsEndOfWord(){
        BasicTrieNode trieNode = new BasicTrieNode();
        trieNode.setEndOfWord(true);
        assertTrue(trieNode.isEndOfWord);
        trieNode.setEndOfWord(false);
        assertFalse(trieNode.isEndOfWord);
    }

    @Test
    void getChildren(){
        BasicTrieNode trieNode = new BasicTrieNode();
        assertEquals(trieNode.children, trieNode.getChildren());
    }

}
