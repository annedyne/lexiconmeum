package com.annepolis.lexiconmeum.shared.model.grammar.partofspeech;

public sealed interface SubtypeDetails extends PartOfSpeechDetails
        permits PronounDetails, DeterminerDetails {

    SyntacticSubtype getSyntacticSubtype();
}
