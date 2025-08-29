package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.InflectionTableDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LexemeDetailResponse {


    String lemma;
    UUID lexemeId;
    GrammaticalPosition position;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> definitions = new ArrayList<>();

    //INFLECTION RELATED FIELDS
    String inflectionClass;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> principalParts = new ArrayList<>();

    @JsonProperty("inflectionTable")
    InflectionTableDTO inflectionTableDTO;

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public GrammaticalPosition getPosition() {
        return position;
    }

    public void setPosition(GrammaticalPosition position) {
        this.position = position;
    }

    public UUID getLexemeId() {
        return lexemeId;
    }

    public void setLexemeId(UUID lexemeId) {
        this.lexemeId = lexemeId;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public void addDefinition(String definition) {
        this.getDefinitions().add(definition);
    }


    public String getInflectionClass() {
        return inflectionClass;
    }

    public void setInflectionClass(String inflectionClass) {
        this.inflectionClass = inflectionClass;
    }

    public List<String> getPrincipalParts() {
        return principalParts;
    }

    public void addPrincipalPart(String principalPart) {
        this.getPrincipalParts().add(principalPart);
    }


    public InflectionTableDTO getInflectionTableDTO() {
        return inflectionTableDTO;
    }

    public void setInflectionTableDTO(InflectionTableDTO inflectionTableDTO) {
        this.inflectionTableDTO = inflectionTableDTO;
    }
}
