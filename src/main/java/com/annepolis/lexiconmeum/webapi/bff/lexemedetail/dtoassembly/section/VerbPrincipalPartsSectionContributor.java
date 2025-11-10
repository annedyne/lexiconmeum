package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.MorphologicalSubtype;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component("verbPrincipalPartsSectionContributor")
class VerbPrincipalPartsSectionContributor implements LexemeDetailSectionContributor {
    private final InflectionKey inflectionKey;

    public VerbPrincipalPartsSectionContributor(InflectionKey inflectionKey) {
        this.inflectionKey = inflectionKey;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getPartOfSpeech() == PartOfSpeech.VERB;
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

        MorphologicalSubtype subtype;
        if(lexeme.getPartOfSpeechDetails() instanceof VerbDetails details){
            subtype = details.getMorphologicalSubtype();
            if(subtype == MorphologicalSubtype.DEPONENT){
                Optional.ofNullable(index.get(inflectionKey.buildThirdPrincipalPartKey()))
                        .map(Inflection::getForm)
                        .filter(f -> !f.isBlank())
                        .ifPresent(dto::addPrincipalPart);
            }
        }
        Optional.ofNullable(index.get(inflectionKey.buildThirdPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(f -> !f.isBlank())
                .ifPresent(dto::addPrincipalPart);
    }
}

