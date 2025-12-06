package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ParticipleTableDTO implements InflectionTableDTO  {
   String gender;
   private List<ParticipleTenseDTO> tenses;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTenses(List<ParticipleTenseDTO> tenses) {
        this.tenses = tenses;
    }

    @JsonProperty("participles")
    protected List<ParticipleTenseDTO> getTenses() {
        return tenses;
    }

    public static class ParticipleTenseDTO implements TenseDTO {
        private String defaultName;
        private String altName;
        private DeclensionTableDTO declensionDTO;

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

        public Map<GrammaticalNumber, Map<GrammaticalCase, String>> getDeclensions() {
            return declensionDTO.getInflectionTable();
        }

        public void setDeclensions(DeclensionTableDTO declensionDTO) {
            this.declensionDTO = declensionDTO;
        }
    }
}