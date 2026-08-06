package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

record WiktionaryLexicalEntryKey(String lemma, PartOfSpeech partOfSpeech, String etymologyNumber) {
}
