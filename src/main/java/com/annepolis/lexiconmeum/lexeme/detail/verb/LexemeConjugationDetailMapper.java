package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.*;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.Sense;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class LexemeConjugationDetailMapper {

    InflectionKey inflectionKey;
    LexemeInflectionMapper lexemeConjugationMapper;

    LexemeConjugationDetailMapper(LexemeInflectionMapper lexemeConjugationMapper, InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
        this.lexemeConjugationMapper = lexemeConjugationMapper;
    }

     LexemeDetailResponse toLexemeDetailDTO(Lexeme lexeme){
         LexemeDetailResponse dto = new LexemeDetailResponse();
        populateDefinitions(dto, lexeme.getSenses());

        populatePrincipalParts(dto, lexeme.getInflectionIndex());
        populateInflectionTable(dto, lexeme);
        return dto;
    }

    void populateDefinitions(LexemeDetailResponse dto, List<Sense> senses){
        senses.stream().flatMap(s -> s.getGloss().stream())
                .toList().forEach(dto::addDefinition);
    }

    void populatePrincipalParts(LexemeDetailResponse dto, Map<String, Inflection> inflectionIndex) {
        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildFirstPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank())
                .ifPresent(dto::addPrincipalPart);

        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildSecondPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank()) // Optional: skip blank forms
                .ifPresent(dto::addPrincipalPart);

        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildThirdPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank()) // Optional: skip blank forms
                .ifPresent(dto::addPrincipalPart);
    }

    void populateInflectionTable(LexemeDetailResponse dto, Lexeme lexeme){
        InflectionTableDTO tableDTO = lexemeConjugationMapper.toInflectionTableDTO(lexeme);
        dto.setInflectionTableDTO(tableDTO);
    }


}
