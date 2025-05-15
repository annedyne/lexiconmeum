package com.annepolis.lexiconmeum.textsearch;

import java.util.List;

public class Inflection {

    String form;
    String number;
    String grammaticalCase;
    String gender;
    List<String> tags;

    public Inflection(){}

    public Inflection(String number, String grammaticalCase, String form){
        this.form = form;
        this.number = number;
        this.grammaticalCase = grammaticalCase;
    }

    public void setInflection(String inflection) {
        this.form = inflection;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCase() {
        return grammaticalCase;
    }

    public void setCase(String nounCase) {
        this.grammaticalCase = nounCase;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }



    @Override
    public String toString(){
        return form;
    }
}
