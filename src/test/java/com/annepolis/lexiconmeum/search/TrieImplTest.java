package com.annepolis.lexiconmeum.search;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TrieImplTest {

    @Test
    void constructorAssignsRoot(){
        TrieNode node = new TrieNodeImpl();
        TrieImpl underTest = new TrieImpl(node);
        assertEquals(node, underTest.getRoot());
    }

    @Test
    void insert(){
        String testWord = "testword";
        TrieNode root = new TrieNodeImpl();
        TrieImpl underTest = new TrieImpl(root);
        underTest.insert(testWord);
        Character value = underTest.getRoot().getChildren().get('t').getContent();
        assertEquals('t', value);
    }

    @Test
    void search(){
        String prefix = "test";
        String word = "word";
        String wholeWord = prefix + word;
        TrieNode root = new TrieNodeImpl();
        TrieImpl underTest = new TrieImpl(root);
        underTest.insert(wholeWord);

        List<String> results = underTest.search(prefix, 20);
        assertEquals(wholeWord, results.get(0));
    }

    @Test
    void givenPrefixReturnsAllWords(){
        TrieNode root = new TrieNodeImpl();
        TrieImpl underTest = new TrieImpl(root);
        underTest.insert("amicus");
        underTest.insert("amare");
        underTest.insert("amabilis");
        underTest.insert("amandare");
        underTest.insert("amandatio");
        underTest.insert("amor");

        List<String> results = underTest.search("ama", 20);
        assertEquals(4, results.size());
    }
}
