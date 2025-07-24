package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.Sense;
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

    LexemeConjugationDetailDTO toLexemeDetailDTO(Lexeme lexeme){
        LexemeConjugationDetailDTO dto = new LexemeConjugationDetailDTO();
        populateDefinitions(dto, lexeme.getSenses());

        populatePrincipleParts(dto, lexeme.getInflectionIndex());
        populateInflectionTable(dto, lexeme);
        return dto;
    }

    void populateDefinitions(LexemeConjugationDetailDTO dto, List<Sense> senses){
        senses.stream().flatMap(s -> s.getGloss().stream())
                .toList().forEach(dto::addDefinition);
    }

    void populatePrincipleParts(LexemeConjugationDetailDTO dto, Map<String, Inflection> inflectionIndex) {
        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildFirstPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank())
                .ifPresent(dto::addPrinciplePart);

        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildSecondPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank()) // Optional: skip blank forms
                .ifPresent(dto::addPrinciplePart);

        Optional.ofNullable(inflectionIndex.get(inflectionKey.buildThirdPrincipalPartKey()))
                .map(Inflection::getForm)
                .filter(form -> !form.isBlank()) // Optional: skip blank forms
                .ifPresent(dto::addPrinciplePart);
    }

    void populateInflectionTable(LexemeConjugationDetailDTO dto, Lexeme lexeme){
        InflectionTableDTO tableDTO = lexemeConjugationMapper.toInflectionTableDTO(lexeme);
        dto.setInflectionTableDTO(tableDTO);
    }


}
