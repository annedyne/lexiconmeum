package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.lexeme.detail.BuilderHasGrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;

import java.util.Set;
import java.util.TreeSet;

public class Agreement  implements Inflection  {
    private final String form;
    private final String alternativeForm;
    private final Set<GrammaticalGender> genders;
    private final GrammaticalCase grammaticalCase;
    private final GrammaticalNumber number;
    private final GrammaticalDegree degree;
    private final boolean isAdverb;

    public Agreement (Builder builder){
        this.form = builder.form;
        this.alternativeForm = builder.alternativeForm;
        this.grammaticalCase = builder.grammaticalCase;
        this.number = builder.number;
        this.genders = builder.genders;
        this.degree = builder.degree;
        this.isAdverb = builder.isAdverb;
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
    public Agreement.Builder toBuilder() {
        return new Agreement.Builder(form)
                .setAlternativeForm(alternativeForm)
                .setNumber(number)
                .setGrammaticalCase(grammaticalCase)
                .setGenders(genders)
                .setAdverb(isAdverb);
    }

    public static class Builder implements InflectionBuilder, BuilderHasGrammaticalCase {

        private final String form;
        private String alternativeForm;
        private Set<GrammaticalGender> genders = new TreeSet<>();
        private GrammaticalNumber number;
        private GrammaticalCase grammaticalCase;
        private GrammaticalDegree degree = GrammaticalDegree.POSITIVE;
        private boolean isAdverb = false;

        public Builder(String form) {
            this.form = form;
        }

        @Override
        public Agreement build() {
            return new Agreement(this);
        }

        @Override
        public Agreement.Builder setAlternativeForm(String form) {
            this.alternativeForm = form;
            return this;
        }


        public Agreement.Builder addGender(GrammaticalGender gender) {
            this.genders.add(gender);
            return this;
        }

        public Agreement.Builder setGenders(Set<GrammaticalGender> genders) {
            for(GrammaticalGender gender : genders){
                addGender(gender);
            }
            return this;
        }

        @Override
        public Agreement.Builder setNumber(GrammaticalNumber grammaticalNumber) {
            this.number = grammaticalNumber;
            return this;
        }

        @Override
        public Agreement.Builder setGrammaticalCase(GrammaticalCase grammaticalCase) {
            this.grammaticalCase = grammaticalCase;
            return this;
        }

        public GrammaticalCase getGrammaticalCase(){
            return this.grammaticalCase;
        }

        public Agreement.Builder setDegree(GrammaticalDegree degree){
            this.degree = degree;
            return this;
        }

        public Agreement.Builder setAdverb(boolean adverb) {
            isAdverb = adverb;
            return this;
        }
    }
}
