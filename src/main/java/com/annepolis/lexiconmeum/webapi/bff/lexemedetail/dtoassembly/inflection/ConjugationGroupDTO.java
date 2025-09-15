package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class ConjugationGroupDTO implements InflectionTableDTO {

    @JsonProperty("conjugations")
    private List<ConjugationTableDTO> conjugationTableDTOList;

    ConjugationGroupDTO(List<ConjugationTableDTO> tableDTOList){
        this.conjugationTableDTOList = tableDTOList;
    }

    public List<ConjugationTableDTO> getConjugationTableDTOList() {
        return conjugationTableDTOList;
    }
}
