package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ConjugationGroupDTO implements InflectionTableDTO {

    @JsonProperty("conjugations")
    private List<ConjugationTableDTO> conjugationTableDTOList;

    ConjugationGroupDTO(List<ConjugationTableDTO> tableDTOList){
        this.conjugationTableDTOList = tableDTOList;
    }

    public List<ConjugationTableDTO> getConjugationTableDTOList() {
        return conjugationTableDTOList;
    }
}
