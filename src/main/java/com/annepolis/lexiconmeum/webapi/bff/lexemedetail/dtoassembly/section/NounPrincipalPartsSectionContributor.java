package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component("nounPrincipalPartsSectionContributor")
class NounPrincipalPartsSectionContributor implements LexemeDetailSectionContributor {

    private final InflectionKey inflectionKey;

    public NounPrincipalPartsSectionContributor(final InflectionKey inflectionKey) {
        this.inflectionKey = inflectionKey;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getPartOfSpeech() == PartOfSpeech.NOUN;
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
                .filter(form -> !form.isBlank())
                .ifPresent(dto::addPrincipalPart);
    }
}
