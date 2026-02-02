package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

public record POSPrimaryKeyData(String lemma, PartOfSpeech partOfSpeech, String etymologyNumber){ }