package com.annepolis.lexiconmeum.webapi.bff.textsearch.app;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.index.AutocompleteIndex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AutocompleteServiceTest {

    @Mock
    AutocompleteIndex routedAutocompleteIndex;

    @Test
    void testWordEnrichment(){
        AutocompleteService underTest = new AutocompleteService(routedAutocompleteIndex, new SuggestionMapper(),
                getLexemeProviderStub(List.of()), new CommonSuggestionParentExtractor());
        List<FormMatch> rawWords = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            FormMatch match = new FormMatch("test" + i, UUID.randomUUID() );
            rawWords.add(match);
        }
        List<SuggestionResponse> result = underTest.mapMatchesToSuggestionResponses(rawWords);
        assertNotNull(result);
    }

    @Test
    void skipsMatchesWithoutSuggestionParent(){
        Lexeme withParent = new LexemeBuilder("amo", PartOfSpeech.VERB, "1").build();
        Lexeme withoutParent = new LexemeBuilder("sum", PartOfSpeech.VERB, "1").build();
        SuggestionParentExtractor extractor = lexeme ->
                lexeme.equals(withParent) ? Optional.of("amāre") : Optional.empty();

        AutocompleteService underTest = new AutocompleteService(routedAutocompleteIndex, new SuggestionMapper(),
                getLexemeProviderStub(List.of(withParent, withoutParent)), extractor);

        List<SuggestionResponse> result = underTest.mapMatchesToSuggestionResponses(List.of(
                new FormMatch("amo", withParent.getId()),
                new FormMatch("sum", withoutParent.getId())
        ));

        assertEquals(1, result.size());
        assertEquals("amo", result.get(0).getWord());
        assertEquals("amāre", result.get(0).getSuggestionParent());
    }

    LexemeReader getLexemeProviderStub(List<Lexeme> lexemes){
        Map<UUID, Lexeme> byId = lexemes.stream().collect(Collectors.toMap(Lexeme::getId, Function.identity()));
        return  new LexemeReader() {
            @Override
            public Optional<Lexeme> getLexemeIfPresent(UUID lemmaId) {
                return Optional.ofNullable(byId.get(lemmaId));
            }
            @Override
            public Lexeme getLexemeOfType(UUID lemmaId, PartOfSpeech expectedType) {
                return null;
            }

        };
    }
}
