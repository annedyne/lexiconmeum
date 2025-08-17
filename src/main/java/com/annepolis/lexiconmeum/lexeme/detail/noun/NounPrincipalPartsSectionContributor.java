package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailSectionContributor;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class NounPrincipalPartsSectionContributor implements LexemeDetailSectionContributor {

    private final InflectionKey inflectionKey;

    public NounPrincipalPartsSectionContributor(final InflectionKey inflectionKey) {
        this.inflectionKey = inflectionKey;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getGrammaticalPosition() == GrammaticalPosition.NOUN;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        Map<String, Inflection> inflectionIndex = lexeme.getInflectionIndex();
        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildFirstDeclensionPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank())
                .ifPresent(dto::addPrincipalPart);

        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildSecondDeclensionPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank()) // Optional: skip blank forms
                .ifPresent(dto::addPrincipalPart);
    }
}
