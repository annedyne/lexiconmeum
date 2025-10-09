package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeechDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.InflectionKey;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LexemeBuilder {

    private final UUID id;
    private final String lemma;
    private String canonicalForm;
    private final PartOfSpeech partOfSpeech;
    private final String etymologyNumber;
    private GrammaticalGender gender;
    private PartOfSpeechDetails partOfSpeechDetails;
    private final List<Sense> senses = new ArrayList<>();
    private final Map<String, Inflection> inflectionIndex = new HashMap<>();
    private final Set<InflectionClass> inflectionClasses = new TreeSet<>();

    public LexemeBuilder(String lemma, PartOfSpeech partOfSpeech, String etymologyNumber){
        this.lemma = lemma;
        this.partOfSpeech = partOfSpeech;
        this.etymologyNumber = etymologyNumber;
        this.id = computeId( lemma, partOfSpeech, etymologyNumber);
    }

    private static UUID computeId(String lemma, PartOfSpeech partOfSpeech, String etymologyNumber){
        return UUID.nameUUIDFromBytes(computeId(lemma, partOfSpeech.name(), etymologyNumber).getBytes(StandardCharsets.UTF_8));
    }

    private static String computeId(String lemma, String partOfSpeech, String etymologyNumber) {
        return lemma + "#" + partOfSpeech + "#" + etymologyNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getLemma() {
        return lemma;
    }

    public String getCanonicalForm() {
        return canonicalForm;
    }

    public void setCanonicalForm(String canonicalForm) {
        this.canonicalForm = canonicalForm;
    }

    public PartOfSpeech getPartOfSpeech(){
        return this.partOfSpeech;
    }

    public String getEtymologyNumber() {
        return etymologyNumber;
    }

    public LexemeBuilder setGender(GrammaticalGender gender){
        this.gender = gender;
        return this;
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

        if (lemma == null || partOfSpeech == null) {
            throw new IllegalStateException("Missing required fields: " +
                    (lemma == null ? "lemma " : "") +
                    (partOfSpeech == null ? "partOfSpeech " : ""));
        }

        return new Lexeme(this);
    }



}
