package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

public interface LinkableData {

    String getLemma();
    String getLinkingLemma();

    default String getLinkingLemmaWithMacrons() {
        return getLinkingLemma();
    }

    Lexeme link(Lexeme lexeme);

    default String getDataKey() {
        return getLemma();
    }

    PartOfSpeech getParentLinkPartOfSpeech();

}
