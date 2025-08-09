package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.*;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LexemeConjugationDetailMapper extends AbstractLexemeDetailMapper {

    InflectionKey inflectionKey;
    LexemeInflectionMapper lexemeConjugationMapper;

    LexemeConjugationDetailMapper(LexemeInflectionMapper lexemeConjugationMapper, InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
        this.lexemeConjugationMapper = lexemeConjugationMapper;
    }

    @Override
    protected void populatePrincipalParts(LexemeDetailResponse dto, Map<String, Inflection> inflectionIndex) {
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


    @Override
    protected void setInflectionClass(LexemeDetailResponse dto, Lexeme lexeme) {
        String displayTag = lexeme.getInflectionClasses().stream()
                .map(InflectionClass::getDisplayTag)
                .collect(Collectors.joining(" & "));


        dto.setInflectionClass(displayTag + " " + "conjugation");
    }

    @Override
    protected InflectionTableDTO buildTable(Lexeme lexeme) {
        return lexemeConjugationMapper.toInflectionTableDTO(lexeme);
    }
}
