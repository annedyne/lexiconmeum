package com.annepolis.lexiconmeum.webapi.bff.textsearch.domain;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Extracts searchable forms from verb lexemes, including both conjugations
 * and participle inflections.
 */
@Component
public class SearchableVerbFormsExtractor implements SearchableFormsExtractor {

    private final SearchableInflectedFormsExtractor baseExtractor;

    public SearchableVerbFormsExtractor(SearchableInflectedFormsExtractor baseExtractor) {
        this.baseExtractor = baseExtractor;
    }

    @Override
    public Set<String> getSearchableForms(Lexeme lexeme) {
        Set<String> forms = new LinkedHashSet<>();

        // Get regular conjugation forms using base extractor
        forms.addAll(baseExtractor.getSearchableForms(lexeme));

        // Add participle forms if present
        if (lexeme.getPartOfSpeechDetails() instanceof VerbDetails verbDetails) {
            extractParticipleForms(verbDetails, forms);
        }

        return Set.copyOf(forms);
    }

    /**
     * Extract all inflected forms from all participle sets
     */
    private void extractParticipleForms(VerbDetails verbDetails, Set<String> forms) {
        verbDetails.getParticiples().values().forEach(participleSet -> {
            // Extract each inflected form (and its alternative if present)
            participleSet.getInflectionIndex().values().forEach(participle -> {
                Optional.ofNullable(participle.getForm())
                        .filter(s -> !s.isBlank())
                        .ifPresent(forms::add);

                Optional.ofNullable(participle.getAlternativeForm())
                        .filter(s -> !s.isBlank())
                        .ifPresent(forms::add);
            });
        });
    }
}
