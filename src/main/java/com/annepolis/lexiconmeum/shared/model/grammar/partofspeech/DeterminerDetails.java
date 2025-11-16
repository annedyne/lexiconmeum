package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

public record DeterminerDetails( SyntacticSubtype syntacticSubtype)
        implements SyntacticSubtypeDetails {

    @Override
    public SyntacticSubtype getSyntacticSubtype() {
        return syntacticSubtype;
    }
}
