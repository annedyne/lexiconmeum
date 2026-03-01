package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AutocompleteServiceSpringTest {

    private final AutocompleteUseCase underTest;

    AutocompleteServiceSpringTest(AutocompleteUseCase textSearchService) {
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
        assertEquals(10, result.size());

    }

    @Test
    void getWordsStartingWithReturnsOnlyLemmaFormsOrSingleInflectedMatch(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("brevis", 10);
        assertEquals(3, result.size());

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

    @Test
    void determinerLoadedIntoSearch(){

        List<SuggestionResponse> result = underTest.getWordsStartingWith("ille", 10);

        assertEquals(1, result.size());
        assertEquals(PartOfSpeech.DETERMINER, result.get(0).getPartOfSpeech());
    }

    @Test
    void pronounLoadedIntoSearch(){
        String pronoun = "quis";
        List<SuggestionResponse> result = underTest.getWordsStartingWith(pronoun, 10);
        SuggestionResponse response = result.stream()
                .filter(r -> r.getPartOfSpeech().equals(PartOfSpeech.PRONOUN))
                .findAny()
                .orElseThrow(() -> new AssertionError("'Autocomplete Suggestion for 'quis' PRONOUN not found"));
        assertEquals(PartOfSpeech.PRONOUN, response.getPartOfSpeech());
        assertEquals(pronoun, response.getWord());
    }

    @Test
    void esseLoadedIntoSearch(){
        String irregularVerb = "sum";
        List<SuggestionResponse> result = underTest.getWordsStartingWith(irregularVerb, 10);
        SuggestionResponse response = result.stream()
                .filter(r -> r.getPartOfSpeech().equals(PartOfSpeech.VERB))
                .findAny()
                .orElseThrow(() -> new AssertionError("'Autocomplete Suggestion for 'sum' VERB not found"));
        assertEquals(PartOfSpeech.VERB, response.getPartOfSpeech());
        assertEquals(irregularVerb, response.getWord());
    }

}