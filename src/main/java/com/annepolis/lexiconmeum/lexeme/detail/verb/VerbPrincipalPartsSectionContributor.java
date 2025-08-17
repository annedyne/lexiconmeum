package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailSectionContributor;
import com.annepolis.lexiconmeum.shared.model.Inflection;
import com.annepolis.lexiconmeum.shared.model.InflectionKey;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class VerbPrincipalPartsSectionContributor implements LexemeDetailSectionContributor {
    private final InflectionKey inflectionKey;

    public VerbPrincipalPartsSectionContributor(InflectionKey inflectionKey) {
        this.inflectionKey = inflectionKey;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getGrammaticalPosition() == GrammaticalPosition.VERB;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        Map<String, Inflection> index = lexeme.getInflectionIndex();
        Optional.ofNullable(index.get(inflectionKey.buildFirstPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(f -> !f.isBlank())
                .ifPresent(dto::addPrincipalPart);

        Optional.ofNullable(index.get(inflectionKey.buildSecondPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(f -> !f.isBlank())
                .ifPresent(dto::addPrincipalPart);

        Optional.ofNullable(index.get(inflectionKey.buildThirdPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(f -> !f.isBlank())
                .ifPresent(dto::addPrincipalPart);
    }
}

