package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.lexeme.detail.AbstractLexemeDetailMapper;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LexemeAgreementDetailMapper extends AbstractLexemeDetailMapper {

    InflectionKey inflectionKey;
    LexemeAgreementMapper lexemeAgreementMapper;

    LexemeAgreementDetailMapper(LexemeAgreementMapper lexemeAgreementMapper, InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
        this.lexemeAgreementMapper = lexemeAgreementMapper;
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
        return lexemeAgreementMapper.toInflectionTableDTO(lexeme);
    }
}
