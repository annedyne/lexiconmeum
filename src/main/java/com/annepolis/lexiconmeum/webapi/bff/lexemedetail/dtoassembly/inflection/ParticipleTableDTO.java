package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ParticipleTable")
public class ParticipleTableDTO implements InflectionTableDTO  {

    private String gender;
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

    @JsonProperty("tenses")
    protected List<ParticipleTenseDTO> getTenses() {
        return tenses;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ParticipleTenseDTO implements TenseDTO {
        private String defaultName;
        private String altName;
        private DeclensionTableDTO declensionDTO;
        private Map<GrammaticalCase, List<String>> formsByCase;

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
            return declensionDTO == null ? null : declensionDTO.getInflectionTable();
        }

        public void setDeclensions(DeclensionTableDTO declensionDTO) {
            this.declensionDTO = declensionDTO;
        }

        public Map<GrammaticalCase, List<String>> getFormsByCase() {
            return formsByCase;
        }

        public void setFormsByCase(Map<GrammaticalCase, List<String>> formsByCase) {
            this.formsByCase = formsByCase;
        }
    }
}
