package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;

import java.util.*;

/**
 * Equality and hashCode are based on lemma, position, and etymologyNumber,
 * which together define the identity of a Lexeme. The `id` field is derived
 * from these and not included in equality checks.
 */
public class Lexeme {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private final String etymologyNumber;
    private final Set<InflectionClass> inflectionClasses;
    private final List<Sense> senses;
    private final Map<String, Inflection> inflections;

    Lexeme(LexemeBuilder builder ) {
        this.lemma = builder.getLemma();
        this.position = builder.getPosition();
        this.etymologyNumber = builder.getEtymologyNumber();
        this.id = builder.getId();
        this.inflections = builder.getInflections();
        this.senses = builder.getSenses();
        this.inflectionClasses = builder.getInflectionClasses();
    }

    public UUID getId() {
        return id;
    }

    public String getLemma() { return lemma; }

    public GrammaticalPosition getPosition() {
        return position;
    }

    public Set<InflectionClass> getInflectionClasses() {
        return inflectionClasses;
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
        return Objects.equals(lemma, lexeme.lemma)
                && Objects.equals(position, lexeme.position)
                && Objects.equals(etymologyNumber, lexeme.etymologyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemma, position, etymologyNumber);
    }

    @Override
    public String toString() {
        return String.format("Lexeme{id='%s', lemma='%s', position='%s', etymology-number = }", id, lemma, position, etymologyNumber);
    }

}
