package com.annepolis.lexiconmeum.webapi.bff.textsearch.index;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TrieAutocompleteIndexTest {

    @Test
    void constructorAssignsRoot(){

        TrieAutocompleteIndex underTest = new TrieAutocompleteIndex();
        assertNotNull(underTest.getRoot());
    }


    @Test
    void trieReturnsAllMatchingWordsGivenPrefix(){
        String prefix = "test";
        String word = prefix + "word";
        LexemeBuilder dLexemeBuilder = new LexemeBuilder(word, PartOfSpeech.NOUN, "1");
        Lexeme lexeme = dLexemeBuilder.build();

        LexemeBuilder cLexemeBuilder = new LexemeBuilder( word, PartOfSpeech.VERB, "1");
        Lexeme lexeme2 = cLexemeBuilder.build();

        TrieAutocompleteIndex underTest = new TrieAutocompleteIndex();
        underTest.insert(lexeme.getLemma(), lexeme.getId());
        underTest.insert(lexeme2.getLemma(), lexeme2.getId());

        List<FormMatch> results = underTest.searchForMatchingForms(prefix, 20);

        assertEquals(word, results.get(1).form());
        assertEquals( lexeme.getId(), results.get(1).lexemeId());

        assertEquals(word, results.get(0).form());
        assertEquals( lexeme2.getId(), results.get(0).lexemeId());
    }

    @Test
    void givenPrefixReturnsAllUniqueMatches(){
        TrieAutocompleteIndex underTest = new TrieAutocompleteIndex();
        Lexeme lexeme = TestUtil.getNewTestNounLexeme();
        for (Inflection inflection : lexeme.getInflections()){
            underTest.insert(inflection.getForm(), lexeme.getId());
        }
        List<FormMatch> results = underTest.searchForMatchingForms("amico", 20);
        assertEquals(3, results.size());
    }

    @Test
    void givenSuffixReturnsAllUniqueMatches(){
        TrieAutocompleteIndex underTest = new TrieAutocompleteIndex();
        List<Lexeme> lexemes = TestUtil.getMixedPositionTestLexemes();


        for(Lexeme lexeme : lexemes) {

            for (String word : lexeme.getInflections().stream().map(
                    inflection -> new StringBuilder(inflection.getForm()).reverse().toString()).toList()){
                underTest.insert(word, lexeme.getId());
            }
        }
        List<FormMatch> results = underTest.searchForMatchingForms(new StringBuilder("is").reverse().toString(), 20);
        assertEquals(6, results.size());
    }

}
