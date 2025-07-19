package com.annepolis.lexiconmeum.lexeme.detail;

public interface Inflection <T extends Inflection<T>>{

    String getForm();
    String getAlternativeForm();
    InflectionBuilder<T> toBuilder();

}
