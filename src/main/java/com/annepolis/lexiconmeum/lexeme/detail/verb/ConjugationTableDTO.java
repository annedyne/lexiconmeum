package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.InflectionTableDTO;

import java.util.List;

public class ConjugationTableDTO implements InflectionTableDTO {

    private String voice;
    private String mood;
    private List<TenseDTO> tenses;

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

   public List<TenseDTO> getTenses() {
        return tenses;
    }

    void setTenses(List<TenseDTO> tenses) {
        this.tenses = tenses;
    }

    public static class TenseDTO {
        private String defaultName;
        private String altName;
        private List<String> forms;

        public String getDefaultName() {
            return defaultName;
        }

        public void setDefaultName(String defaultName) {
            this.defaultName = defaultName;
        }

        public String getAltName() {
            return altName;
        }

        public void setAltName(String altName) {
            this.altName = altName;
        }

        public List<String> getForms() {
            return forms;
        }

        public void setForms(List<String> forms) {
            this.forms = forms;
        }
    }



}

