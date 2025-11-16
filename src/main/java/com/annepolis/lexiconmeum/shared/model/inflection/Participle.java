package com.annepolis.lexiconmeum.shared.model.inflection;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;

import java.util.Set;
import java.util.TreeSet;

public class Participle implements Inflection {

    private final String form;
    private final String alternativeForm;
    private final Set<GrammaticalGender> genders;
    private final GrammaticalCase grammaticalCase;
    private final GrammaticalNumber number;
    private final GrammaticalDegree degree;
    
    public Participle(Builder builder){
        this.form = builder.form;
        this.alternativeForm = builder.alternativeForm;
        this.grammaticalCase = builder.grammaticalCase;
        this.number = builder.number;
        this.genders = builder.genders;
        this.degree = builder.degree;
    }

    @Override
    public String getForm() {
        return this.form;
    }

    @Override
    public String getAlternativeForm() {
        return this.alternativeForm;
    }

    public GrammaticalCase getGrammaticalCase() {
        return grammaticalCase;
    }

    public GrammaticalNumber getNumber() {
        return number;
    }

    public Set<GrammaticalGender> getGenders() {
        return genders;
    }

    public GrammaticalDegree getDegree() {
        return degree;
    }

    @Override
    public InflectionBuilder toBuilder() {
        return new Participle.Builder(form)
                .setAlternativeForm(alternativeForm)
                .setGrammaticalCase(grammaticalCase)
                .setNumber(number)
                .setGenders(genders);
    }


    public static class Builder implements InflectionBuilder, BuilderHasGrammaticalCase {
        private final String form;
        private String alternativeForm;
        private Set<GrammaticalGender> genders = new TreeSet<>();
        private GrammaticalNumber number;
        private GrammaticalCase grammaticalCase;
        private GrammaticalDegree degree = GrammaticalDegree.POSITIVE;

        public Builder(String form) {
            this.form = form;
        }

        @Override
        public Participle build() {
            return new Participle(this);
        }

        protected String getForm() {
            return form;
        }

        @Override
        public Participle.Builder setAlternativeForm(String form) {
            this.alternativeForm = form;
            return this;
        }

        public Participle.Builder addGender(GrammaticalGender gender) {
            this.genders.add(gender);
            return this;
        }

        public Participle.Builder setGenders(Set<GrammaticalGender> genders) {
            for(GrammaticalGender gender : genders){
                addGender(gender);
            }
            return this;
        }

        @Override
        public Participle.Builder setNumber(GrammaticalNumber grammaticalNumber) {
            this.number = grammaticalNumber;
            return this;
        }

        @Override
        public Participle.Builder setGrammaticalCase(GrammaticalCase grammaticalCase) {
            this.grammaticalCase = grammaticalCase;
            return this;
        }

        public Participle.Builder setDegree(GrammaticalDegree degree){
            this.degree = degree;
            return this;
        }

    }
}
