package com.annepolis.lexiconmeum.domain.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrieNodeImplTest {

    @Test
    void constructorTest(){
        TrieNodeImpl trieNode = new TrieNodeImpl();
        Assertions.assertFalse(trieNode.isEndOfWord);

        assertNotNull(trieNode.children);
    }

    @Test
    void setIsEndOfWord(){
        TrieNodeImpl trieNode = new TrieNodeImpl();
        trieNode.setEndOfWord(true);
        assertTrue(trieNode.isEndOfWord);
        trieNode.setEndOfWord(false);
        assertFalse(trieNode.isEndOfWord);
    }

    @Test
    void getChildren(){
        TrieNodeImpl trieNode = new TrieNodeImpl();
        assertEquals(trieNode.children, trieNode.getChildren());
    }


}
