package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

public sealed interface SyntacticSubtypeDetails extends PartOfSpeechDetails
        permits PronounDetails, DeterminerDetails {

    SyntacticSubtype getSyntacticSubtype();
}
