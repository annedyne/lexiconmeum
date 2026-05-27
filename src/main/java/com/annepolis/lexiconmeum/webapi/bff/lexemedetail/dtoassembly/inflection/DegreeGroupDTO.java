package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@Schema(description = "Contains positive, comparative, and superlative forms of a given adjective")
class DegreeGroupDTO implements InflectionTableDTO {

    private final AgreementTableDTO positive;
    private final AgreementTableDTO comparative;
    private final AgreementTableDTO superlative;

    DegreeGroupDTO(AgreementTableDTO positive, AgreementTableDTO comparative, AgreementTableDTO superlative) {
        this.positive = positive;
        this.comparative = comparative;
        this.superlative = superlative;
    }

    public List<AgreementEntryDTO> getPositive() {
        return Optional.ofNullable(positive)
                .map(AgreementTableDTO::getAgreements)
                .orElse(List.of());
    }

    public List<AgreementEntryDTO> getComparative() {
        return Optional.ofNullable(comparative)
                .map(AgreementTableDTO::getAgreements)
                .orElse(List.of());
    }

    public List<AgreementEntryDTO> getSuperlative() {
        return Optional.ofNullable(superlative)
                .map(AgreementTableDTO::getAgreements)
                .orElse(List.of());
    }
}
