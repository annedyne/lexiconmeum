package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TextSearchServiceTest {

    TextSearchSuggestionService underTest;

    @BeforeEach
    void init(@Mock  TextSearchTrieIndexService textSearchTrieIndexService ){
        underTest = new TextSearchSuggestionService(textSearchTrieIndexService, new TextSearchSuggestionMapper(), getLexemeProviderStub());
    }

    @Test
    void testWordEnrichment(){
        TextSearchSuggestionMapper mapper = new TextSearchSuggestionMapper();

        List<String> rawWords = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String wordString = mapper.toFormIdString("test" + i, UUID.randomUUID());
            rawWords.add(wordString);
        }
        List<TextSearchSuggestionDTO> result = underTest.enrichRawWords(rawWords);
        assertNotNull(result);
    }

    LexemeProvider getLexemeProviderStub(){
        return  new LexemeProvider() {
            @Override
            public Optional<Lexeme> getLexemeIfPresent(UUID lemmaId) {
                return Optional.empty();
            }
            @Override
            public Lexeme getLexemeOfType(UUID lemmaId, GrammaticalPosition expectedType) {
                return null;
            }
        };
    }
}
