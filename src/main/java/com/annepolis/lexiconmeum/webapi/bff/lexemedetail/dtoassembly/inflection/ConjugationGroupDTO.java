package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Contains conjugation and participle inflection tables for a given verb")
public class ConjugationGroupDTO implements InflectionTableDTO {
    private final List<ConjugationTableDTO> conjugationTableDTOList;
    private final List<ParticipleTableDTO> participleTableDTOList;

    ConjugationGroupDTO(List<ConjugationTableDTO> tableDTOList, List<ParticipleTableDTO> participleTableDTOList){
        this.conjugationTableDTOList = tableDTOList;
        this.participleTableDTOList = participleTableDTOList;
    }



    @JsonProperty("conjugations")
    public List<ConjugationTableDTO> getConjugationTableDTOList() {
        return conjugationTableDTOList;
    }

    @JsonProperty("participles")
    public List<ParticipleTableDTO> getParticipleDTOList(){
        return participleTableDTOList;
    }


}
