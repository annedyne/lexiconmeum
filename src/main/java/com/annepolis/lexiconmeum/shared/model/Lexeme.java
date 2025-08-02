package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Lexeme {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private final InflectionClass inflectionClass;
    private final List<Sense> senses;
    private final Map<String, Inflection> inflections;

    Lexeme(LexemeBuilder builder ) {
        this.lemma = builder.getLemma();
        this.position = builder.getPosition();
        this.id = builder.getId();
        this.inflections = builder.getInflections();
        this.senses = builder.getSenses();
        this.inflectionClass = builder.getInflectionClass();
    }

    public UUID getId() {
        return id;
    }

    public String getLemma() { return lemma; }

    public GrammaticalPosition getPosition() {
        return position;
    }

    public InflectionClass getInflectionClass() {
        return inflectionClass;
    }

    public List<Inflection> getInflections(){
        return inflections.values().stream().toList();
    }

    public Map<String, Inflection> getInflectionIndex(){
        return inflections;
    }

    public List<Sense> getSenses() { return senses; }

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
