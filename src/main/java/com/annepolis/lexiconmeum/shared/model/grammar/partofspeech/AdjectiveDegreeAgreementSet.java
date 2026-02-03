package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;

import java.util.Map;

public class AdjectiveDegreeAgreementSet {

    private String degreeLemma;
    private GrammaticalDegree grammaticalDegree;
    private Map<String, Inflection> inflectionIndex;

    public AdjectiveDegreeAgreementSet(String degreeLemma, GrammaticalDegree grammaticalDegree){
        this.degreeLemma = degreeLemma;
        this.grammaticalDegree = grammaticalDegree;
    }

    public String getDegreeLemma() {
        return degreeLemma;
    }

    public void setDegreeLemma(String degreeLemma) {
        this.degreeLemma = degreeLemma;
    }

    public GrammaticalDegree getGrammaticalDegree() {
        return grammaticalDegree;
    }

    public void setGrammaticalDegree(GrammaticalDegree grammaticalDegree) {
        this.grammaticalDegree = grammaticalDegree;
    }

    public Map<String, Inflection> getInflectionIndex() {
        return inflectionIndex;
    }

    public void setInflectionIndex(Map<String, Inflection> inflectionIndex) {
        this.inflectionIndex = inflectionIndex;
    }
}
