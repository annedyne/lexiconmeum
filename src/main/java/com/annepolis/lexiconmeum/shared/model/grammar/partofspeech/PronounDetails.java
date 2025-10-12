package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

public record PronounDetails( SyntacticSubtype syntacticSubtype)
        implements SubtypeDetails {

    @Override
    public SyntacticSubtype getSyntacticSubtype() {
        return syntacticSubtype;
    }
}
