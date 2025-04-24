package com.annepolis.lexiconmeum.domain.model;

import java.util.List;

public class Inflection {

    String inflection;
    List<String> tags;

    public void setInflection(String inflection) {
        this.inflection = inflection;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
