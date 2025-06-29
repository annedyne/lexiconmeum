package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.InflectionBuilder;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.*;

import java.util.ArrayList;
import java.util.List;

public class Conjugation implements Inflection {

    private final String form;
    private final String alternateForm;
    private final GrammaticalVoice voice;
    private final GrammaticalMood mood;
    private final GrammaticalTense tense;
    private final GrammaticalPerson person;
    private final GrammaticalNumber number;


    public Conjugation(Builder builder){
        this.voice = builder.getVoice();
        this.mood = builder.getMood();
        this.tense = builder.getTense();
        this.person = builder.getPerson();
        this.number = builder.getNumber();
        this.form = builder.getForm();
        this.alternateForm = builder.getForm();
    }

    @Override
    public String getForm() {
        return form;
    }

    public String getAlternateForm() { return alternateForm; }
    public GrammaticalVoice getVoice() { return voice; }
    public GrammaticalTense getTense() { return tense; }
    public GrammaticalPerson getPerson() { return person;}
    public GrammaticalNumber getNumber() { return number; }
    public GrammaticalMood getMood() { return mood; }

    public Builder toBuilder() {
        return new Builder(form)
                .setVoice(voice)
                .setTense(tense)
                .setPerson(person)
                .setNumber(number)
                .setMood(mood)
                .setAlternateForm(alternateForm);
    }
    public static class Builder implements InflectionBuilder<Conjugation> {

        private GrammaticalVoice voice;
        private GrammaticalMood mood;
        private GrammaticalTense tense;
        private GrammaticalPerson person;
        private GrammaticalNumber number;

        private final String form;
        private String alternateForm;

        public Builder(String form) {
            this.form = form;
        }

        public GrammaticalVoice getVoice() {
            return voice;
        }

        public Conjugation.Builder setVoice(GrammaticalVoice voice) {
            this.voice = voice;
            return this;
        }

        public GrammaticalPerson getPerson() {
            return person;
        }

        public Conjugation.Builder setPerson(GrammaticalPerson person) {
            this.person = person;
            return this;
        }

        public GrammaticalNumber getNumber() {
            return number;
        }

        public Conjugation.Builder setNumber(GrammaticalNumber number) {
            this.number = number;
            return this;
        }

        public GrammaticalTense getTense() {
            return tense;
        }

        public Conjugation.Builder setTense(GrammaticalTense tense) {
            this.tense = tense;
            return this;
        }

        public GrammaticalMood getMood() {
            return mood;
        }

        public Conjugation.Builder setMood(GrammaticalMood mood) {
            this.mood = mood;
            return this;
        }

        public String getForm() {
            return form;
        }

        public String getAlternateForm() {
            return alternateForm;
        }

        public Conjugation.Builder setAlternateForm(String alternateForm) {
            this.alternateForm = alternateForm;
            return this;
        }

        @Override
        public Conjugation build() {
            validateFields();
            return new Conjugation(this);
        }

        private void validateFields() {
            List<String> missing = new ArrayList<>();

            if (mood == GrammaticalMood.INFINITIVE) {
                if (tense == null) missing.add("tense");
                if (form == null) missing.add("form");
            } else {
                if (person == null) missing.add("person");
                if (number == null) missing.add("number");
                if (tense == null) missing.add("tense");
                if (form == null) missing.add("form");
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required fields: " + String.join(", ", missing));
            }
        }
    }
}
