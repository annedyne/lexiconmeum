package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.lexeme.detail.verb.InflectionKey;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LexemeBuilder<T extends Inflection> {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private GrammaticalGender gender;
    private final List<Sense> senses = new ArrayList<>();
    private final Map<String, T> inflectionIndex = new HashMap<>();

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

    public LexemeBuilder<T> setGender(GrammaticalGender gender){
        this.gender = gender;
        return this;
    }

    public GrammaticalGender getGender() {
        return gender;
    }

    public LexemeBuilder<T> addSense(Sense sense){
        this.senses.add(sense);
        return this;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public LexemeBuilder<T> addInflection(T inflection){
        String key = InflectionKey.of(inflection);
        if (inflectionIndex.containsKey(key)) {
            T existing = inflectionIndex.get(key);
            InflectionBuilder<T>  builder = existing.toBuilder();
            T updated = builder.setAlternativeForm(inflection.getForm()).build();
            inflectionIndex.put(key, updated);
        } else {
            inflectionIndex.put(key, inflection);
        }
        return this;
    }

    public Map<String,T> getInflections() {
        return inflectionIndex;
    }


    public Lexeme<T> build(){

        if (lemma == null || position == null) {
            throw new IllegalStateException("Missing required fields: " +
                    (lemma == null ? "lemma " : "") +
                    (position == null ? "position " : ""));
        }

        return new Lexeme<>(this);
    }


}
