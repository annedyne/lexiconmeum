package com.annepolis.lexiconmeum.textsearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class InMemoryLexiconSpringTest {

    private final TextSearchComponent underTest;

    InMemoryLexiconSpringTest(TextSearchComponent textSearchComponent) {
        this.underTest = textSearchComponent;
    }

    @Test
    void testWiring(){
        assertInstanceOf(TextSearchTrieCacheComponent.class, underTest);
    }

    @Test
    void getWordsStartingWithReturnsExpectedResultsFromCache(){

        List<String> result = underTest.getWordsStartingWith("am", 10);
        assertEquals(10, result.size());

        result = underTest.getWordsStartingWith("amara", 10);
        assertEquals(6, result.size());

    }

    @Test
    void getWordsEndingWithReturnsCachedResults(){
        List<String> result = underTest.getWordsEndingWith("eris", 10);

        assertEquals(4, result.size());

    }
}
