package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.textsearch.Inflection;

import java.util.ArrayList;
import java.util.List;

public class Lexeme {

    private String lemma;
    private String position;
    private List<Sense> senses;
    private List<Inflection> inflections;

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void addSense(Sense sense){
        if(senses == null){
            senses = new ArrayList<>();
        }
        senses.add(sense);
    }

    public void setInflections(List<Inflection> inflections) {
        this.inflections = inflections;
    }

    public List<Inflection> getInflections(){
        if(inflections == null){
            inflections = new ArrayList<>();
        }
        return inflections;
    }


}
