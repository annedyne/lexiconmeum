package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

public interface LinkableData {

    String getLemma();
    String getLinkingLemma();
    String getLinkingLemmaWithMacrons();
    Lexeme link(Lexeme lexeme);
    String getDataKey();
    PartOfSpeech getParentLinkPartOfSpeech();

}
