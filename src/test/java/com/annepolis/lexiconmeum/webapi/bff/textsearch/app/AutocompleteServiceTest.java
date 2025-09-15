package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.index.AutocompleteIndex;
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
class AutocompleteServiceTest {

    AutocompleteService underTest;

    @BeforeEach
    void init(@Mock AutocompleteIndex routedAutocompleteIndex){
        underTest = new AutocompleteService(routedAutocompleteIndex, new SuggestionMapper(), getLexemeProviderStub());
    }

    @Test
    void testWordEnrichment(){
        List<FormMatch> rawWords = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            FormMatch match = new FormMatch("test" + i, UUID.randomUUID() );
            rawWords.add(match);
        }
        List<SuggestionResponse> result = underTest.mapMatchesToSuggestionResponses(rawWords);
        assertNotNull(result);
    }

    LexemeReader getLexemeProviderStub(){
        return  new LexemeReader() {
            @Override
            public Optional<Lexeme> getLexemeIfPresent(UUID lemmaId) {
                return Optional.empty();
            }
            @Override
            public Lexeme getLexemeOfType(UUID lemmaId, PartOfSpeech expectedType) {
                return null;
            }
        };
    }
}
