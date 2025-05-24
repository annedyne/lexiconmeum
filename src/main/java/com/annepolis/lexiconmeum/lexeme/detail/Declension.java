package com.annepolis.lexiconmeum.lexeme.detail;

public class Declension implements Inflection {


    private String form;
    private GrammaticalCase grammaticalCase;
    private GrammaticalNumber number;
    private GrammaticalGender gender;

    public Declension(){}
    public Declension(GrammaticalGender gender, GrammaticalNumber number, GrammaticalCase grammaticalCase, String form){
        this.number = number;
        this.grammaticalCase = grammaticalCase;
        this.gender = gender;
        this.form = form;
    }

    public GrammaticalCase getGrammaticalCase() {
        return grammaticalCase;
    }

    public void setGrammaticalCase(GrammaticalCase grammaticalCase) {
        this.grammaticalCase = grammaticalCase;
    }

    public GrammaticalNumber getNumber() {
        return number;
    }

    public void setNumber(GrammaticalNumber number) {
        this.number = number;
    }

    public GrammaticalGender getGender() {
        return gender;
    }

    public void setGender(GrammaticalGender gender) {
        this.gender = gender;
    }

    @Override
    public String getForm() {
        return form;
    }

    @Override
    public void setForm(String form) {
        this.form = form;
    }
}
