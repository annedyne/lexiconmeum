package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.BuilderHasGrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;

public class Declension implements Inflection {


    private final String form;
    private final GrammaticalCase grammaticalCase;
    private final GrammaticalNumber number;

    public Declension(Builder builder){
        this.number = builder.number;
        this.grammaticalCase = builder.grammaticalCase;
        this.form = builder.form;
    }

    public GrammaticalCase getGrammaticalCase() {
        return grammaticalCase;
    }

    public GrammaticalNumber getNumber() {
        return number;
    }

    @Override
    public String getForm() {
        return form;
    }

    @Override
    public String getAlternativeForm() {
        return null;
    }

    @Override
    public Declension.Builder toBuilder() {
        return new Declension.Builder(form)
                .setNumber(number)
                .setGrammaticalCase(grammaticalCase);
    }

    @Override
    public String toString(){
        return getForm();
    }


    public static class Builder implements InflectionBuilder, BuilderHasGrammaticalCase {

        private GrammaticalNumber number;
        private GrammaticalCase grammaticalCase;
        private final String form;

        public Builder(String form) {
            this.form = form;
        }

        public Declension.Builder setNumber(GrammaticalNumber number) {
            this.number = number;
            return this;
        }

        @Override
        public Declension.Builder setAlternativeForm(String form) {
            return this;
        }

        @Override
        public Declension.Builder setGrammaticalCase(GrammaticalCase grammaticalCase) {
            this.grammaticalCase = grammaticalCase;
            return this;
        }

        @Override
        public Declension build() {
            if (number == null || grammaticalCase == null || form == null) {
                throw new IllegalStateException("Missing required fields: " +
                        (number == null ? "number " : "") +
                        (grammaticalCase == null ? "case " : "") +
                        (form == null ? "form" : ""));
            }
            return new Declension(this);
        }
    }
}
