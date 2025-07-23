package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;

public interface InflectionBuilder {
    Inflection build();

    InflectionBuilder setNumber(GrammaticalNumber grammaticalNumber);
    InflectionBuilder setAlternativeForm(String form);
}

