package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.AbstractLexemeDetailMapper;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LexemeDeclensionDetailMapper extends AbstractLexemeDetailMapper {

    InflectionKey inflectionKey;
    LexemeInflectionMapper lexemeDeclensionMapper;

    LexemeDeclensionDetailMapper(LexemeInflectionMapper lexemeDeclensionMapper, InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
        this.lexemeDeclensionMapper = lexemeDeclensionMapper;
    }

    @Override
    protected void setInflectionClass(LexemeDetailResponse dto, Lexeme lexeme) {
        String displayTag = lexeme.getInflectionClasses().stream()
                .map(InflectionClass::getDisplayTag)
                .collect(Collectors.joining(" & "));


        dto.setInflectionClass(displayTag + " " + "declension");
    }

    @Override
    protected InflectionTableDTO buildTable(Lexeme lexeme) {
        return lexemeDeclensionMapper.toInflectionTableDTO(lexeme);
    }

    @Override
    protected void populatePrincipalParts(LexemeDetailResponse dto, Map<String, Inflection> inflectionIndex) {
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
