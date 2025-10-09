package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeechDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;

import java.util.*;

/**
 * Equality and hashCode are based on lemma, partOfSpeech, and etymologyNumber,
 * which together define the identity of a Lexeme. The `id` field is derived
 * from these and not included in equality checks.
 */
public class Lexeme {

    private final UUID id;

    // word without macrons
    private final String lemma;

    // Canonical form with macrons
    private final String canonicalForm;
    private final PartOfSpeech partOfSpeech;
    private final PartOfSpeechDetails partOfSpeechDetails;
    private final String etymologyNumber;
    private final Set<InflectionClass> inflectionClasses;
    private final List<Sense> senses;
    private final Map<String, Inflection> inflections;

    Lexeme(LexemeBuilder builder) {
        this.lemma = builder.getLemma();
        this.partOfSpeech = builder.getPartOfSpeech();
        this.etymologyNumber = builder.getEtymologyNumber();
        this.id = builder.getId();
        this.inflections = builder.getInflections();
        this.senses = builder.getSenses();
        this.inflectionClasses = builder.getInflectionClasses();
        this.partOfSpeechDetails = builder.getPartOfSpeechDetails();
        this.canonicalForm = builder.getCanonicalForm();
    }

    public UUID getId() {
        return id;
    }

    public String getLemma() { return lemma; }

    public String getCanonicalForm() {
        return canonicalForm == null ? getLemma() : canonicalForm;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public PartOfSpeechDetails getPartOfSpeechDetails() {
        return partOfSpeechDetails;
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
                && Objects.equals(partOfSpeech, lexeme.partOfSpeech)
                && Objects.equals(etymologyNumber, lexeme.etymologyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemma, partOfSpeech, etymologyNumber);
    }

    @Override
    public String toString() {
        return String.format("Lexeme{id='%s', lemma='%s', partOfSpeech='%s', etymology-number='%s'}", id, lemma, partOfSpeech, etymologyNumber);
    }

}
