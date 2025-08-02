package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

public class AgreementTableDTO implements InflectionTableDTO {

    @JsonProperty("agreements")
    Map<Set<GrammaticalGender>, Map<GrammaticalNumber, Map<GrammaticalCase, String>>> agreementTable;

    public void setInflectionTable(Map<Set<GrammaticalGender>, Map<GrammaticalNumber, Map<GrammaticalCase, String>>> agreementTable) {
        this.agreementTable = agreementTable;
    }
}

