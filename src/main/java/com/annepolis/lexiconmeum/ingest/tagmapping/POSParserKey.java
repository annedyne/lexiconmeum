package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryHeadTemplate;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.Arrays;
import java.util.Optional;

public enum POSParserKey {

    ADJECTIVE_POSITIVE(PartOfSpeech.ADJECTIVE.name(), WiktionaryHeadTemplate.ADJECTIVE_POSITIVE.getName(), true),
    ADJECTIVE_COMPARATIVE(PartOfSpeech.ADJECTIVE.name(),WiktionaryHeadTemplate.ADJECTIVE_COMPARATIVE.getName(), false),
    ADJECTIVE_SUPERLATIVE(PartOfSpeech.ADJECTIVE.name(),WiktionaryHeadTemplate.ADJECTIVE_SUPERLATIVE.getName(), false),
    VERB(PartOfSpeech.VERB.name(),WiktionaryHeadTemplate.VERB.getName(), true),
    PARTICIPLE(PartOfSpeech.VERB.name(),WiktionaryHeadTemplate.PARTICIPLE.getName(), false);

    private final String posTag;
    private final String templateHeadName;
    private final boolean isLemma;

    POSParserKey(final String posTag, final String templateHeadName, boolean isLemma) {
        this.posTag = posTag;
        this.templateHeadName = templateHeadName;
        this.isLemma = isLemma;
    }

    public String getTemplateHeadName(){
        return templateHeadName;
    }
    public boolean isLemma(){
        return isLemma;
    }

    public static Optional<POSParserKey> resolve(String posTag, String templateHeadName) {
        return Arrays.stream(values())
                .filter(ParserKeyFactory ->
                    ParserKeyFactory.posTag.equalsIgnoreCase(posTag)
                            && ParserKeyFactory.templateHeadName.equalsIgnoreCase(templateHeadName)
                )
                .findFirst();
    }
}
