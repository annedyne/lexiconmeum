package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class AdjectiveDetails implements PartOfSpeechDetails {

    AdjectiveTerminationType terminationType;
    Map<GrammaticalDegree, AdjectiveDegreeAgreementSet> degreeAgreementSets;

    public AdjectiveDetails(Builder builder){
        this.terminationType = builder.adjectiveTerminationType;
        this.degreeAgreementSets = Collections.unmodifiableMap(new TreeMap<>(builder.degreeInflectionSets));
    }

    public AdjectiveTerminationType getTerminationType() {
        return terminationType;
    }

    public Map<GrammaticalDegree, AdjectiveDegreeAgreementSet> getDegreeInflections(){
        return degreeAgreementSets;
    }

    public AdjectiveDetails.Builder toBuilder() {
        AdjectiveDetails.Builder builder = new AdjectiveDetails.Builder().setAdjectiveTerminationType(terminationType);
        getDegreeInflections().values().forEach(builder::addDegreeInflectionSet);
        return builder;
    }

    public static class Builder {
        private AdjectiveTerminationType adjectiveTerminationType;
        private final Map<GrammaticalDegree, AdjectiveDegreeAgreementSet> degreeInflectionSets = new TreeMap<>();

        public Builder setAdjectiveTerminationType(AdjectiveTerminationType adjectiveTerminationType) {
            this.adjectiveTerminationType = adjectiveTerminationType;
            return this;
        }

        public Builder addDegreeInflectionSet(AdjectiveDegreeAgreementSet degreeInflectionSet){
            degreeInflectionSets.put(degreeInflectionSet.getGrammaticalDegree(), degreeInflectionSet);
            return this;
        }

        public AdjectiveDetails build(){
            return new AdjectiveDetails(this);
        }
    }


}


