package com.annepolis.lexiconmeum.textsearch;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TextSearchIndexTest {

    @Test
    void constructorAssignsRoot(){

        TextSearchIndex underTest = new TextSearchIndex();
        assertNotNull(underTest.getRoot());
    }


    @Test
    void trieReturnsWordGivenPrefix(){
        String prefix = "test";
        String word = "word";
        String wholeWord = prefix + word;
        TextSearchIndex underTest = new TextSearchIndex();
        underTest.insert(wholeWord);

        List<String> results = underTest.search(prefix, 20);
        assertEquals(wholeWord, results.get(0));
    }

    @Test
    void givenPrefixReturnsAllMatches(){
        TextSearchIndex underTest = new TextSearchIndex();
        List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");
        underTest.insert(inputs);
        List<String> results = underTest.search("ama", 20);
        assertEquals(4, results.size());
    }

    @Test
    void givenSuffixReturnsAllMatches(){
        TextSearchIndex underTest = new TextSearchIndex();
        List<String> inputs = List.of("amicus", "amare", "amabilis", "amandare", "amandatio", "amor");
        underTest.insert(inputs.stream().map( word -> new StringBuilder(word).reverse().toString()).toList());
        List<String> results = underTest.search("er", 20);
        assertEquals(2, results.size());
    }
}
