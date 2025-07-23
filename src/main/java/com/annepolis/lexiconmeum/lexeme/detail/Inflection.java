package com.annepolis.lexiconmeum.lexeme.detail;

public interface Inflection {

    String getForm();
    String getAlternativeForm();
    InflectionBuilder toBuilder();

}
