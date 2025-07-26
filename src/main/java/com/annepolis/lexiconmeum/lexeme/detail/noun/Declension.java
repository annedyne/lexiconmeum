package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;

public class Declension implements Inflection {


    private final String form;
    private final GrammaticalCase grammaticalCase;
    private final GrammaticalNumber number;

    public Declension(Builder builder){
        this.number = builder.getNumber();
        this.grammaticalCase = builder.getGrammaticalCase();
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
    public InflectionBuilder toBuilder() {
        return new Declension.Builder(form)
                .setNumber(number)
                .setGrammaticalCase(grammaticalCase);
    }

    @Override
    public String toString(){
        return getForm();
    }


    public static class Builder implements InflectionBuilder {

        private GrammaticalNumber number;
        private GrammaticalCase grammaticalCase;
        private final String form;

        public Builder(String form) {
            this.form = form;
        }

        public GrammaticalNumber getNumber() {
            return number;
        }

        public Declension.Builder setNumber(GrammaticalNumber number) {
            this.number = number;
            return this;
        }

        @Override
        public InflectionBuilder setAlternativeForm(String form) {
            return this;
        }

        public GrammaticalCase getGrammaticalCase() {
            return grammaticalCase;
        }


        public Declension.Builder setGrammaticalCase(GrammaticalCase grammaticalCase) {
            this.grammaticalCase = grammaticalCase;
            return this;
        }

        public String getForm() {
            return form;
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
