package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdjectiveDegreeAgreementSet {

    private final String degreeLemma;
    private final GrammaticalDegree grammaticalDegree;
    private final Set<InflectionClass> inflectionClasses;
    private Map<String, Inflection> inflectionIndex = new HashMap<>();

    public AdjectiveDegreeAgreementSet(String degreeLemma, GrammaticalDegree grammaticalDegree, Set<InflectionClass> inflectionClasses){
        this.degreeLemma = degreeLemma;
        this.grammaticalDegree = grammaticalDegree;
        this.inflectionClasses = inflectionClasses;
    }

    public String getDegreeLemma() {
        return degreeLemma;
    }

    public Set<InflectionClass> getInflectionClasses() {
        return inflectionClasses;
    }

    public GrammaticalDegree getGrammaticalDegree() {
        return grammaticalDegree;
    }

    public List<Inflection> getInflections(){
        return inflectionIndex.values().stream().toList();
    }
    public Map<String, Inflection> getInflectionIndex() {
        return inflectionIndex;
    }



    public void setInflectionIndex(Map<String, Inflection> inflectionIndex) {
        this.inflectionIndex = inflectionIndex;
    }

}
