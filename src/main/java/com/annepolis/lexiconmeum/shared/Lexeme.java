package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Lexeme {

    private final UUID id;
    private String lemma;
    private String position;
    private List<Sense> senses = new ArrayList<>();
    private List<Inflection> inflections = new ArrayList<>();

    public Lexeme(String lemma, String position) {
        this.lemma = lemma;
        this.position = position;
        this.id = UUID.nameUUIDFromBytes(computeId(lemma, position).getBytes(StandardCharsets.UTF_8));
    }

    private static String computeId(String lemma, String position) {
        return lemma + "#" + position;
    }

    public UUID getId() {
        return id;
    }

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
        senses.add(sense);
    }

    public void setInflections(List<Inflection> inflections) {
        this.inflections = inflections;
    }

    public List<Inflection> getInflections(){
        return inflections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lexeme lexeme = (Lexeme) o;
        return Objects.equals(lemma, lexeme.lemma) && Objects.equals(position, lexeme.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemma, position);
    }

    @Override
    public String toString() {
        return String.format("Lexeme{id='%s', lemma='%s', position='%s'}", id, lemma, position);
    }
}
