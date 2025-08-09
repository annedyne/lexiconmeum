package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;

public interface InflectionBuilder {
    Inflection build();

    InflectionBuilder setNumber(GrammaticalNumber grammaticalNumber);
    InflectionBuilder setAlternativeForm(String form);
}

