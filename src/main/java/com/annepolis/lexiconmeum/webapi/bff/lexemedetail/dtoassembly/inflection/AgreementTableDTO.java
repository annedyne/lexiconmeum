package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Agreement Table implementation")
public class AgreementTableDTO implements InflectionTableDTO {
    private List<AgreementEntryDTO> agreements;

    public List<AgreementEntryDTO> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<AgreementEntryDTO> agreements) {
        this.agreements = agreements;
    }

}

