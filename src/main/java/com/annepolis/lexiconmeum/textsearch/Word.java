package com.annepolis.lexiconmeum.textsearch;

import java.util.List;

class Word {

    String word;
    String definition;
    String position;
    List<Inflection> inflections;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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
