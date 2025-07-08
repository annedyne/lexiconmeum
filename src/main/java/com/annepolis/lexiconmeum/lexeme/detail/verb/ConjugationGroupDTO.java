package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;

import java.util.List;

public class ConjugationGroupDTO implements InflectionTableDTO {

    private List<ConjugationTableDTO> conjugationTableDTOList;

    ConjugationGroupDTO(List<ConjugationTableDTO> tableDTOList){
        this.conjugationTableDTOList = tableDTOList;
    }

    public List<ConjugationTableDTO> getConjugationTableDTOList() {
        return conjugationTableDTOList;
    }
}
