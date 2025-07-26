package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.lexeme.detail.verb.InflectionKey;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.Sense;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class LexemeDeclensionDetailMapper {

    InflectionKey inflectionKey;
    LexemeInflectionMapper lexemeDeclensionMapper;

    LexemeDeclensionDetailMapper(LexemeInflectionMapper lexemeDeclensionMapper, InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
        this.lexemeDeclensionMapper = lexemeDeclensionMapper;
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
        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildFirstDeclensionPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank())
                .ifPresent(dto::addPrincipalPart);

        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildSecondDeclensionPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank()) // Optional: skip blank forms
                .ifPresent(dto::addPrincipalPart);
    }

    void populateInflectionTable(LexemeDetailResponse dto, Lexeme lexeme){
        InflectionTableDTO tableDTO = lexemeDeclensionMapper.toInflectionTableDTO(lexeme);
        dto.setInflectionTableDTO(tableDTO);
    }
}
