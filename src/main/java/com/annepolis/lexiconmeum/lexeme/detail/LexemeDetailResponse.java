package com.annepolis.lexiconmeum.lexeme.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LexemeDetailResponse {

    String inflectionClass;
    String lemma;
    List<String> principalParts = new ArrayList<>();
    List<String> definitions = new ArrayList<>();

    public String getInflectionClass() {
        return inflectionClass;
    }

    public void setInflectionClass(String inflectionClass) {
        this.inflectionClass = inflectionClass;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

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
