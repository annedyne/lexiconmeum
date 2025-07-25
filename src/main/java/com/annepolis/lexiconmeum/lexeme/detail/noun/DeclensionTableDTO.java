package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.EnumMap;
import java.util.Map;

/**
 * DTO representing a declension table.
 * <p>
 * Table format:
 * Map<Number (e.g. "SINGULAR"), Map<GrammaticalCase (e.g. "NOMINATIVE"), Form (e.g. "pōculum">>
 */
public class DeclensionTableDTO implements InflectionTableDTO {

    @JsonProperty("declensions")
    Map<GrammaticalNumber, Map<GrammaticalCase, String>> table  = new EnumMap<>(GrammaticalNumber.class);

    public void setInflectionTable(Map<GrammaticalNumber, Map<GrammaticalCase, String>> table) {
        this.table = table;
    }

    public Map<GrammaticalNumber, Map<GrammaticalCase, String>> getInflectionTable() {
        return table;
    }
}
