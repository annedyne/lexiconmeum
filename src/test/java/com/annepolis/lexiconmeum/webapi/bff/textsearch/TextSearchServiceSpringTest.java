package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TextSearchServiceSpringTest {

    private final TextSearchSuggestionService underTest;

    TextSearchServiceSpringTest(TextSearchSuggestionService textSearchService) {
        this.underTest = textSearchService;
    }

    @Test
    void testWiring(){
        assertInstanceOf(TextSearchSuggestionService.class, underTest);
    }

    @Test
    void getWordsStartingWithReturnsExpectedResultsFromCache(){

        List<TextSearchSuggestionDTO> result = underTest.getWordsStartingWith("am", 10);
        assertEquals(10, result.size());

        result = underTest.getWordsStartingWith("amarem", 10);
        assertEquals(4, result.size());

    }

    @Test
    void getWordsEndingWithReturnsUniqueCachedResults(){
        List<TextSearchSuggestionDTO> result = underTest.getWordsEndingWith("eris", 10);
        assertEquals(5, result.size());

    }

    @Test
    void getWordsStartingWithNoDupes(){

        List<TextSearchSuggestionDTO> result = underTest.getWordsStartingWith("brevis", 10);
        assertEquals(4, result.size());

        result = underTest.getWordsStartingWith("amarem", 10);
        assertEquals(4, result.size());

    }

    @Test
    void adverbsLoadedIntoSearch(){

        List<TextSearchSuggestionDTO> result = underTest.getWordsStartingWith("coram", 10);
        assertEquals(1, result.size());

        result = underTest.getWordsStartingWith("celeriter", 10);
        assertEquals(1, result.size());

    }

    @Test
    void nounsLoadedIntoSearch(){

        List<TextSearchSuggestionDTO> result = underTest.getWordsStartingWith("pocula", 10);
        assertEquals(1, result.size());

        result = underTest.getWordsStartingWith("cora", 10);
        assertEquals(1, result.size());

    }

    @Test
    void verbsLoadedIntoSearch(){

        List<TextSearchSuggestionDTO> result = underTest.getWordsStartingWith("amabam", 10);
        assertEquals(4, result.size());

        result = underTest.getWordsStartingWith("amamus", 10);
        assertEquals(1, result.size());

    }

}