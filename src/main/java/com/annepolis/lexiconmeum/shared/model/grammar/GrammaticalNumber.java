package com.annepolis.lexiconmeum.shared.model.grammar;

// Order is important (affects api response)
public enum GrammaticalNumber {
    SINGULAR,
    PLURAL;

    public String getTag() {
        return this.name().toLowerCase();
    }
}
