package com.annepolis.lexiconmeum.lexeme.detail;

import java.util.List;

public class Conjugation implements Inflection {

    String form;
    List<String> tags;

    @Override
    public String getForm() {
        return null;
    }

    @Override
    public void setForm(String form) {
        this.form = form;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
