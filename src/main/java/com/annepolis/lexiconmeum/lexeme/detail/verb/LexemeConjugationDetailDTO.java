package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;

import java.util.ArrayList;
import java.util.List;

public class LexemeConjugationDetailDTO {

    List<String> principleParts = new ArrayList<>();
    List<String> definitions = new ArrayList<>();

    InflectionTableDTO inflectionTableDTO;

    public List<String> getPrincipleParts() {
        return principleParts;
    }

    public void addPrinciplePart(String principlePart) {
        this.getPrincipleParts().add(principlePart);
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
