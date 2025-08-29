package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TextSearchTrieIndexTest {

    @Test
    void constructorAssignsRoot(){

        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        assertNotNull(underTest.getRoot());
    }


    @Test
    void trieReturnsAllMatchingWordsGivenPrefix(){
        String prefix = "test";
        String word = prefix + "word";
        LexemeBuilder dLexemeBuilder = new LexemeBuilder(word, GrammaticalPosition.NOUN, "1");
        Lexeme lexeme = dLexemeBuilder.build();

        LexemeBuilder cLexemeBuilder = new LexemeBuilder( word, GrammaticalPosition.VERB, "1");
        Lexeme lexeme2 = cLexemeBuilder.build();

        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        underTest.insert(lexeme.getLemma(), lexeme.getId());
        underTest.insert(lexeme2.getLemma(), lexeme2.getId());

        List<String> results = underTest.searchForMatchingForms(prefix, 20);

        assertTrue(results.contains(lexeme.getLemma() + ": " + lexeme.getId()));
        assertTrue(results.contains(lexeme2.getLemma() + ": " + lexeme2.getId()));
    }

    @Test
    void givenPrefixReturnsAllUniqueMatches(){
        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        Lexeme lexeme = TestUtil.getNewTestNounLexeme();
        for (Inflection inflection : lexeme.getInflections()){
            underTest.insert(inflection.getForm(), lexeme.getId());
        }
        List<String> results = underTest.searchForMatchingForms("amico", 20);
        assertEquals(3, results.size());
    }

    @Test
    void givenSuffixReturnsAllUniqueMatches(){
        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        List<Lexeme> lexemes = TestUtil.getMixedPositionTestLexemes();


        for(Lexeme lexeme : lexemes) {

            for (String word : lexeme.getInflections().stream().map(
                    inflection -> new StringBuilder(inflection.getForm()).reverse().toString()).toList()){
                underTest.insert(word, lexeme.getId());
            }
        }
        List<String> results = underTest.searchForMatchingForms(new StringBuilder("is").reverse().toString(), 20);
        assertEquals(6, results.size());
    }

}
