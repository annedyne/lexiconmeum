package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class ConjugationGroupDTO implements InflectionTableDTO {

    @JsonProperty("conjugations")
    private List<ConjugationTableDTO> conjugationTableDTOList;

    @JsonProperty("participles")
    private List<ParticipleTableDTO> participleTableDTOList;

    ConjugationGroupDTO(List<ConjugationTableDTO> tableDTOList, List<ParticipleTableDTO> participleTableDTOList){
        this.conjugationTableDTOList = tableDTOList;
        this.participleTableDTOList = participleTableDTOList;
    }

    public List<ConjugationTableDTO> getConjugationTableDTOList() {
        return conjugationTableDTOList;
    }

    public List<ParticipleTableDTO> getParticipleDTOList(){
        return participleTableDTOList;
    }


}
