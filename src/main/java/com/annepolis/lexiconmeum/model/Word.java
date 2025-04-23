package com.annepolis.lexiconmeum.model;

import java.util.List;

public class Word {

    String definition;
    String position;
    List<Inflection> inflections;

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setInflections(List<Inflection> inflections) {
        this.inflections = inflections;
    }
}
