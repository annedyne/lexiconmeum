package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Lexeme<T extends Inflection> {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private final List<Sense> senses;
    private final Map<String, T> inflections;

    Lexeme(LexemeBuilder<T> builder ) {
        this.lemma = builder.getLemma();
        this.position = builder.getPosition();
        this.id = builder.getId();
        this.inflections = builder.getInflections();
        this.senses = builder.getSenses();
    }

    public UUID getId() {
        return id;
    }

    public String getLemma() { return lemma; }

    public GrammaticalPosition getPosition() {
        return position;
    }

    public List<T> getInflections(){
        return inflections.values().stream().toList();
    }

    public Map<String, T> getInflectionIndex(){
        return inflections;
    }

    public List<Sense> getSenses() { return senses; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lexeme<T> lexeme = (Lexeme) o;
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
