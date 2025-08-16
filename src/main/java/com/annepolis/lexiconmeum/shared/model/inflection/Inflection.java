package com.annepolis.lexiconmeum.shared.model.inflection;

public interface Inflection {

    String getForm();
    String getAlternativeForm();
    InflectionBuilder toBuilder();

}
