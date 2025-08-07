package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;

import java.util.List;
import java.util.Map;

public class AgreementEntryDTO {

    private List<GrammaticalGender> genders;
    private Map<GrammaticalNumber, Map<GrammaticalCase, String>> inflections;

    public List<GrammaticalGender> getGenders() {
        return genders;
    }

    public void setGenders(List<GrammaticalGender> genders) {
        this.genders = genders;
    }

    public Map<GrammaticalNumber, Map<GrammaticalCase, String>> getInflections() {
        return inflections;
    }

    public void setInflections(Map<GrammaticalNumber, Map<GrammaticalCase, String>> inflections) {
        this.inflections = inflections;
    }
}
