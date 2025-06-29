package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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
        LexemeBuilder lexemeBuilder = new LexemeBuilder(word, GrammaticalPosition.NOUN);
        Lexeme lexeme = lexemeBuilder.build();

        lexemeBuilder = new LexemeBuilder( word, GrammaticalPosition.VERB);
        Lexeme lexeme2 = lexemeBuilder.build();

        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        underTest.insert(lexeme.getLemma(), lexeme.getId());
        underTest.insert(lexeme2.getLemma(), lexeme2.getId());

        List<String> results = underTest.search(prefix, 20);
        assertEquals(lexeme.getLemma() + ": " + lexeme.getId(), results.get(0));

        assertEquals(lexeme2.getLemma() + ": " + lexeme2.getId(), results.get(1));
    }

    @Test
    void givenPrefixReturnsAllMatches(){
        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        Lexeme lexeme = TestUtil.getNewTestNounLexeme();
        for (Inflection inflection : lexeme.getInflections()){
            underTest.insert(inflection.getForm(), lexeme.getId());
        }
        List<String> results = underTest.search("amico", 20);
        assertEquals(2, results.size());
    }

    @Test
    void givenSuffixReturnsAllMatches(){
        TextSearchTrieIndex underTest = new TextSearchTrieIndex(new TextSearchSuggestionMapper());
        List<Lexeme> lexemes = TestUtil.getMixedPositionTestLexemes();
        for(Lexeme lexeme : lexemes) {
            for (String word : lexeme.getInflections().stream().map( inflection -> new StringBuilder(inflection.getForm()).reverse().toString()).toList()){
                    underTest.insert(word, lexeme.getId());
            }
        }
        List<String> results = underTest.search(new StringBuilder("is").reverse().toString(), 20);
        assertEquals(6, results.size());
    }


}
