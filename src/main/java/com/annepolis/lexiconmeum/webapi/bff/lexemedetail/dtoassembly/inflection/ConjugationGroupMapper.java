package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;

import java.util.List;

public class ConjugationGroupMapper implements InflectionTableMapper {

    ConjugationTableMapper conjugationTableMapper;
    ParticipleTableMapper participleTableMapper;

    public ConjugationGroupMapper(ConjugationTableMapper conjugationTableMapper, ParticipleTableMapper participleTableMapper){
        this.conjugationTableMapper = conjugationTableMapper;
        this.participleTableMapper = participleTableMapper;
    }

    @Override
    public InflectionTableDTO toInflectionTableDTO(Lexeme lexeme) {
        List<ConjugationTableDTO> conjugationDTO = conjugationTableMapper.toInflectionTableDTO(lexeme);
        List<ParticipleTableDTO> participleTableDTO = participleTableMapper.toInflectionTableDTO(lexeme);
        return new ConjugationGroupDTO(conjugationDTO, participleTableDTO);
    }
}
