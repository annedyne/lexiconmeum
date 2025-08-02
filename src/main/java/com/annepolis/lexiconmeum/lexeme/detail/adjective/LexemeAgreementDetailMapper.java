package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionKey;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.Sense;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LexemeAgreementDetailMapper {

    InflectionKey inflectionKey;
    LexemeAgreementMapper lexemeAgreementMapper;

    LexemeAgreementDetailMapper(LexemeAgreementMapper lexemeAgreementMapper, InflectionKey inflectionKey){
        this.inflectionKey = inflectionKey;
        this.lexemeAgreementMapper = lexemeAgreementMapper;
    }

    public LexemeDetailResponse toLexemeDetailDTO(Lexeme lexeme){
        LexemeDetailResponse dto = new LexemeDetailResponse();
        populateDefinitions(dto, lexeme.getSenses());

        populateInflectionTable(dto, lexeme);
        return dto;
    }

    void populateDefinitions(LexemeDetailResponse dto, List<Sense> senses){
        senses.stream().flatMap(s -> s.getGloss().stream())
            .toList().forEach(dto::addDefinition);
    }

    void populateInflectionTable(LexemeDetailResponse dto, Lexeme lexeme){
        dto.setInflectionTableDTO(lexemeAgreementMapper.toInflectionTableDTO(lexeme));
    }
}
