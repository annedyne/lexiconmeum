package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;

import java.util.EnumMap;
import java.util.Map;

/**
 * DTO representing a declension table.
 * <p>
 * Table format:
 * Map<Number (e.g. "SINGULAR"), Map<GrammaticalCase (e.g. "NOMINATIVE"), Form (e.g. "pōculum">>
 */
public class DeclensionTableDTO implements InflectionTableDTO {

    Map<GrammaticalNumber, Map<GrammaticalCase, String>> table  = new EnumMap<>(GrammaticalNumber.class);

    public void setTable(Map<GrammaticalNumber, Map<GrammaticalCase, String>> table) {
        this.table = table;
    }

    public Map<GrammaticalNumber, Map<GrammaticalCase, String>> getTable() {
        return table;
    }
}
