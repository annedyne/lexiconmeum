package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;

import java.util.ArrayList;
import java.util.List;

public class LexemeConjugationDetailDTO {

    List<String> principalParts = new ArrayList<>();
    List<String> definitions = new ArrayList<>();

    InflectionTableDTO inflectionTableDTO;

    public List<String> getPrincipleParts() {
        return principalParts;
    }

    public void addPrinciplePart(String principalPart) {
        this.getPrincipleParts().add(principalPart);
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
