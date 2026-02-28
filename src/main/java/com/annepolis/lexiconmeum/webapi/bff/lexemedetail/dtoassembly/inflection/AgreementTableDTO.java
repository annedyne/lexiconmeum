package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import java.util.List;

class AgreementTableDTO implements InflectionTableDTO {

    private List<AgreementEntryDTO> agreements;

    public List<AgreementEntryDTO> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<AgreementEntryDTO> agreements) {
        this.agreements = agreements;
    }

}

