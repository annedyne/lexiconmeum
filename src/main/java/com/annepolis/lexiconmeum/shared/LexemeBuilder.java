package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LexemeBuilder {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private GrammaticalGender gender;
    private List<Sense> senses = new ArrayList<>();
    private List<Inflection> inflections = new ArrayList<>();

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

    public LexemeBuilder setInflectionList(List<Inflection> inflectionList){
        this.inflections = inflectionList;
        return this;
    }

    public LexemeBuilder addInflection(Inflection inflection){
        this.inflections.add(inflection);
        return this;
    }

    public List<Inflection> getInflections() {
        return inflections;
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
