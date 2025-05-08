package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.textsearch.Inflection;

import java.util.List;

public class Lexeme {

    String lemma;
    String definition;
    String position;
    List<Inflection> inflections;

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

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

    public List<Inflection> getInflections(){ return inflections; }


}
