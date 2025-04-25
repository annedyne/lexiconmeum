package com.annepolis.lexiconmeum.domain.trie;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReversibleTrieTest {

    @Test
    void constructorAssignsRoot(){
        TrieNode node = new BasicTrieNode();
        ReversibleTrie underTest = new ReversibleTrie(node);
        assertEquals(node, underTest.getRoot());
    }

    @Test
    void insertProducesExpectedNodeKey(){
        String testWord = "testword";
        TrieNode root = new BasicTrieNode();
        ReversibleTrie underTest = new ReversibleTrie(root);
        underTest.insert(testWord);
        Character value = underTest.getRoot().getChildren().get('t').getContent();
        assertEquals('t', value);
    }

    @Test
    void trieReturnsWordGivenPrefix(){
        String prefix = "test";
        String word = "word";
        String wholeWord = prefix + word;
        TrieNode root = new BasicTrieNode();
        ReversibleTrie underTest = new ReversibleTrie(root);
        underTest.insert(wholeWord);

        List<String> results = underTest.search(prefix, 20);
        assertEquals(wholeWord, results.get(0));
    }

    @Test
    void givenPrefixReturnsAllMatches(){
        TrieNode root = new BasicTrieNode();
        ReversibleTrie underTest = new ReversibleTrie(root);
        List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");
        underTest.insert(inputs);
        List<String> results = underTest.search("ama", 20);
        assertEquals(4, results.size());
    }

    @Test
    void givenSuffixReturnsAllMatches(){
        TrieNode root = new BasicTrieNode();
        ReversibleTrie underTest = new ReversibleTrie(root);
        List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");
        underTest.insert(inputs.stream().map( word -> new StringBuilder(word).reverse().toString()).toList());
        List<String> results = underTest.search("er", 20);
        assertEquals(2, results.size());
    }
}
