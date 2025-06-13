package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;

public interface InflectionBuilder<T extends Inflection> {
    T build();

    InflectionBuilder<T> setNumber(GrammaticalNumber grammaticalNumber);
}

