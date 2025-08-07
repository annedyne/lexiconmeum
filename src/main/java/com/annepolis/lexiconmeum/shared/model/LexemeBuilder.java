package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionKey;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LexemeBuilder {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private GrammaticalGender gender;
    private final List<Sense> senses = new ArrayList<>();
    private final Map<String, Inflection> inflectionIndex = new HashMap<>();
    private final Set<InflectionClass> inflectionClasses = new TreeSet<>();

    public LexemeBuilder(String lemma, GrammaticalPosition position){
        this.lemma = lemma;
        this.position = position;
        this.id = computeId( lemma, position);
    }

    private UUID computeId(String lemma, GrammaticalPosition position){
        return UUID.nameUUIDFromBytes(computeId(lemma, position.name()).getBytes(StandardCharsets.UTF_8));
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

    public GrammaticalPosition getPosition(){
        return this.position;
    }

    public LexemeBuilder setGender(GrammaticalGender gender){
        this.gender = gender;
        return this;
    }

    public GrammaticalGender getGender() {
        return gender;
    }

    public LexemeBuilder addSense(Sense sense){
        this.senses.add(sense);
        return this;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public LexemeBuilder addInflection(Inflection inflection){
        String key = InflectionKey.of(inflection);
        if (inflectionIndex.containsKey(key)) {
            Inflection existing = inflectionIndex.get(key);
            Inflection updated = existing
                    .toBuilder()
                    .setAlternativeForm(inflection.getForm())
                    .build();

            inflectionIndex.put(key, updated);
        } else {
            inflectionIndex.put(key, inflection);
        }
        return this;
    }

    public Map<String,Inflection> getInflections() {
        return inflectionIndex;
    }

    public Set<InflectionClass> getInflectionClasses() {
        return inflectionClasses;
    }

    public LexemeBuilder addInflectionClass(InflectionClass inflectionClass) {
        this.inflectionClasses.add(inflectionClass);
        return this;
    }

    public Lexeme build(){

        if (lemma == null || position == null) {
            throw new IllegalStateException("Missing required fields: " +
                    (lemma == null ? "lemma " : "") +
                    (position == null ? "position " : ""));
        }

        return new Lexeme(this);
    }



}
