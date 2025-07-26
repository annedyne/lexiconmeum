package com.annepolis.lexiconmeum.lexeme.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LexemeDetailResponse {

    List<String> principalParts = new ArrayList<>();
    List<String> definitions = new ArrayList<>();

    @JsonProperty("inflectionTable")
    InflectionTableDTO inflectionTableDTO;

    public List<String> getPrincipalParts() {
        return principalParts;
    }

    public void addPrincipalPart(String principalPart) {
        this.getPrincipalParts().add(principalPart);
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public void addDefinition(String definition) {
        this.getDefinitions().add(definition);
    }

    public InflectionTableDTO getInflectionTableDTO() {
        return inflectionTableDTO;
    }

    public void setInflectionTableDTO(InflectionTableDTO inflectionTableDTO) {
        this.inflectionTableDTO = inflectionTableDTO;
    }
}
