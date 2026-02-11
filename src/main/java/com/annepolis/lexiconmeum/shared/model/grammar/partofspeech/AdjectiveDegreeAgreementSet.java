package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;

import java.util.Map;
import java.util.Set;

public class AdjectiveDegreeAgreementSet {

    final private String degreeLemma;
    final private GrammaticalDegree grammaticalDegree;
    final private Set<InflectionClass> inflectionClasses;
    private Map<String, Inflection> inflectionIndex;

    public AdjectiveDegreeAgreementSet(String degreeLemma, GrammaticalDegree grammaticalDegree, Set<InflectionClass> inflectionClasses){
        this.degreeLemma = degreeLemma;
        this.grammaticalDegree = grammaticalDegree;
        this.inflectionClasses = inflectionClasses;
    }

    public String getDegreeLemma() {
        return degreeLemma;
    }

    public GrammaticalDegree getGrammaticalDegree() {
        return grammaticalDegree;
    }

    public Map<String, Inflection> getInflectionIndex() {
        return inflectionIndex;
    }

    public void setInflectionIndex(Map<String, Inflection> inflectionIndex) {
        this.inflectionIndex = inflectionIndex;
    }

}
