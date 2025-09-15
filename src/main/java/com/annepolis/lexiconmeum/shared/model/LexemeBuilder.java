package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.grammar.PartOfSpeechDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LexemeBuilder {

    private final UUID id;
    private final String lemma;
    private final GrammaticalPosition position;
    private final String etymologyNumber;
    private GrammaticalGender gender;
    private PartOfSpeechDetails partOfSpeechDetails;
    private final List<Sense> senses = new ArrayList<>();
    private final Map<String, Inflection> inflectionIndex = new HashMap<>();
    private final Set<InflectionClass> inflectionClasses = new TreeSet<>();

    public LexemeBuilder(String lemma, GrammaticalPosition position, String etymologyNumber){
        this.lemma = lemma;
        this.position = position;
        this.etymologyNumber = etymologyNumber;
        this.id = computeId( lemma, position, etymologyNumber);
    }

    private static UUID computeId(String lemma, GrammaticalPosition position, String etymologyNumber){
        return UUID.nameUUIDFromBytes(computeId(lemma, position.name(), etymologyNumber).getBytes(StandardCharsets.UTF_8));
    }

    private static String computeId(String lemma, String position, String etymologyNumber) {
        return lemma + "#" + position + "#" + etymologyNumber;
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

    public String getEtymologyNumber() {
        return etymologyNumber;
    }

    public LexemeBuilder setGender(GrammaticalGender gender){
        this.gender = gender;
        return this;
    }

    public GrammaticalGender getGender() {
        return gender;
    }

    public PartOfSpeechDetails getPartOfSpeechDetails() {
        return partOfSpeechDetails;
    }

    public void setPartOfSpeechDetails(PartOfSpeechDetails partOfSpeechDetails) {
        this.partOfSpeechDetails = partOfSpeechDetails;
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
