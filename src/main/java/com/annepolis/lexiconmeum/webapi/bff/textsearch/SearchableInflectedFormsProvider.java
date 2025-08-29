package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SearchableInflectedFormsProvider implements SearchableFormsProvider {

    public Set<String> getSearchableForms(Lexeme lexeme) {
        Set<String> forms = new LinkedHashSet<>();

        // Collect primary and alternate forms
        lexeme.getInflections().forEach(inflection -> {
            Optional.ofNullable(inflection.getForm())
                    .filter(s -> !s.isBlank())
                    .ifPresent(forms::add);

            Optional.ofNullable(inflection.getAlternativeForm())
                    .filter(s -> !s.isBlank())
                    .ifPresent(forms::add);
        });

        return Collections.unmodifiableSet(forms);


    }
}
