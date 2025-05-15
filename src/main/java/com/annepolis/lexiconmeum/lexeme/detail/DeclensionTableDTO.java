package com.annepolis.lexiconmeum.lexeme.detail;

import java.util.HashMap;
import java.util.Map;

public class DeclensionTableDTO {

    Map<String, Map<String, String>> table  = new HashMap<>();

    public void setTable(Map<String, Map<String, String>> table) {
        this.table = table;
    }

    public Map<String, Map<String, String>> getTable() {
        return table;
    }
}
