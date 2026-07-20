package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(name = "ConjugationTable")
class ConjugationTableDTO implements InflectionTableDTO  {

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

    // Simple tenses populate forms; compound tenses populate formsByGender. Only one is set per tense.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TenseDTO {
        private String defaultName;
        private String altName;
        private List<String> forms;
        private Map<String, List<String>> formsByGender;

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

        public Map<String, List<String>> getFormsByGender() {
            return formsByGender;
        }

        public void setFormsByGender(Map<String, List<String>> formsByGender) {
            this.formsByGender = formsByGender;
        }
    }



}

