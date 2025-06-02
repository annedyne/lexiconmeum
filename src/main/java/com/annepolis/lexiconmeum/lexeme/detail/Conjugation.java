package com.annepolis.lexiconmeum.lexeme.detail;

import java.util.List;

public class Conjugation implements Inflection {

    String form;
    List<String> tags;

    public Conjugation(String lemma){
        this.form = lemma;
    }
    @Override
    public String getForm() {
        return form;
    }

    public List<String> getTags() {
        return tags;
    }
}
