package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;

class AgreementTableDTO implements InflectionTableDTO {

    @JsonProperty("agreements")
    private List<AgreementEntryDTO> agreements;
    Map<Set<GrammaticalGender>, Map<GrammaticalNumber, Map<GrammaticalCase, String>>> agreementTable;

    public void setAgreements(List<AgreementEntryDTO> agreements) {
        this.agreements = agreements;
    }

    public void setInflectionTable(Map<Set<GrammaticalGender>, Map<GrammaticalNumber, Map<GrammaticalCase, String>>> agreementTable) {
        this.agreementTable = agreementTable;
    }
}

