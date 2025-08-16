package com.annepolis.lexiconmeum.shared.model;

public interface Inflection {

    String getForm();
    String getAlternativeForm();
    InflectionBuilder toBuilder();

}
