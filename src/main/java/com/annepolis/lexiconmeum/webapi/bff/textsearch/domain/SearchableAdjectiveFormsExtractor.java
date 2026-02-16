package com.annepolis.lexiconmeum.webapi.bff.textsearch.domain;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SearchableAdjectiveFormsExtractor implements SearchableFormsExtractor{

    private final SearchableInflectedFormsExtractor baseExtractor;

    public SearchableAdjectiveFormsExtractor(SearchableInflectedFormsExtractor baseExtractor) {
        this.baseExtractor = baseExtractor;
    }

    @Override
    public Set<String> getSearchableForms(Lexeme lexeme) {

        // Get regular conjugation forms using base extractor
        Set<String> forms = new LinkedHashSet<>(baseExtractor.getSearchableForms(lexeme));

        // Add adjective forms if present
        if (lexeme.getPartOfSpeechDetails() instanceof AdjectiveDetails adjectiveDetails) {
            extractAdjectiveForms(adjectiveDetails, forms);
        }
        return Set.copyOf(forms);
    }

    /**
     * Extract all inflected forms from all adjective sets
     */
    private void extractAdjectiveForms(AdjectiveDetails adjectiveDetails, Set<String> forms) {
        adjectiveDetails.getDegreeInflections().values().forEach(adjectiveDegreeAgreementSet ->
            // Extract each inflected form (and its alternative if present)
            adjectiveDegreeAgreementSet.getInflectionIndex().values().forEach(adjective -> {
                Optional.ofNullable(adjective.getForm())
                        .filter(s -> !s.isBlank())
                        .ifPresent(forms::add);

                Optional.ofNullable(adjective.getAlternativeForm())
                        .filter(s -> !s.isBlank())
                        .ifPresent(forms::add);
            })
       );
    }
}
