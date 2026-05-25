package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DegreeGroupDTO.class, name = "AdjectiveDegrees"),
        @JsonSubTypes.Type(value = DeclensionTableDTO.class, name = "Declensions"),
        @JsonSubTypes.Type(value = ConjugationGroupDTO.class, name = "VerbForms")
})
@Schema(
        description = "Base interface for polymorphic Inflection Tables",
        discriminatorProperty = "type"
)
public interface InflectionTableDTO {}
