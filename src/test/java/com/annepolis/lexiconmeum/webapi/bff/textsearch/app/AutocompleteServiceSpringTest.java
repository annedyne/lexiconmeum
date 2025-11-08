package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AutocompleteServiceSpringTest {

    private final AutocompleteService underTest;

    AutocompleteServiceSpringTest(AutocompleteService textSearchService) {
        this.underTest = textSearchService;
    }

    @Test
    void testWiring(){
        assertInstanceOf(AutocompleteService.class, underTest);
    }

    @Test
    void getWordsStartingWithReturnsExpectedResultsFromCache(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("am", 10);
        assertEquals(10, result.size());

        result = underTest.getWordsStartingWith("amarem", 10);
        assertEquals(4, result.size());

    }

    @Test
    void getWordsEndingWithReturnsUniqueCachedResults(){
        List<SuggestionResponse> result = underTest.getWordsEndingWith("eris", 10);
        assertEquals(6, result.size());

    }

    @Test
    void getWordsStartingWithNoDupes(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("brevis", 10);
        assertEquals(4, result.size());

        result = underTest.getWordsStartingWith("amarem", 10);
        assertEquals(4, result.size());

    }

    @Test
    void adverbsLoadedIntoSearch(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("coram", 10);
        assertEquals(2, result.size());

        result = underTest.getWordsStartingWith("celeriter", 10);
        assertEquals(1, result.size());

    }

    @Test
    void nounsLoadedIntoSearch(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("pocula", 10);
        assertEquals(1, result.size());
    }

    @Test
    void verbsLoadedIntoSearch(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("amabam", 10);
        assertEquals(4, result.size());

        result = underTest.getWordsStartingWith("amamus", 10);
        assertEquals(1, result.size());

    }

}