package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;

import java.util.Arrays;
import java.util.Optional;

public enum POSParserKey {

    ADVERB(PartOfSpeech.ADVERB, WiktionaryHeadTemplate.ADVERB.getName(), true),
    ADJECTIVE_POSITIVE(PartOfSpeech.ADJECTIVE, WiktionaryHeadTemplate.ADJECTIVE_POSITIVE.getName(), true),
    ADJECTIVE_COMPARATIVE(PartOfSpeech.ADJECTIVE,WiktionaryHeadTemplate.ADJECTIVE_COMPARATIVE.getName(), true),
    ADJECTIVE_SUPERLATIVE(PartOfSpeech.ADJECTIVE,WiktionaryHeadTemplate.ADJECTIVE_SUPERLATIVE.getName(), true),
    CONJUNCTION(PartOfSpeech.CONJUNCTION, WiktionaryHeadTemplate.CONJUNCTION.getName(), true),
    DETERMINER(PartOfSpeech.DETERMINER, WiktionaryHeadTemplate.DETERMINER.getName(), true),
    NOUN(PartOfSpeech.NOUN, WiktionaryHeadTemplate.NOUN.getName(), true),
    PREPOSITION(PartOfSpeech.PREPOSITION,  WiktionaryHeadTemplate.PREPOSITION.getName(), true),
    POSTPOSITION(PartOfSpeech.POSTPOSITION,  WiktionaryHeadTemplate.POSTPOSITION.getName(), true),
    PRONOUN(PartOfSpeech.PRONOUN, WiktionaryHeadTemplate.PRONOUN.getName(), true),
    VERB(PartOfSpeech.VERB,WiktionaryHeadTemplate.VERB.getName(), true),
    PARTICIPLE(PartOfSpeech.VERB,WiktionaryHeadTemplate.PARTICIPLE.getName(), false);

    private final PartOfSpeech partOfSpeech;
    private final String HeadTemplateName;
    private final boolean isLemma;

    POSParserKey(final PartOfSpeech partOfSpeech, final String HeadTemplateName, boolean isLemma) {
        this.partOfSpeech = partOfSpeech;
        this.HeadTemplateName = HeadTemplateName;
        this.isLemma = isLemma;
    }

    public String getHeadTemplateName(){
        return HeadTemplateName;
    }
    public boolean isLemma(){
        return isLemma;
    }

    public static Optional<POSParserKey> resolveWithPosTagAndHeadTemplateName(String posTag, String headTemplateName) {
        return Arrays.stream(values())
                .filter(POSParserKey ->
                        POSParserKey.partOfSpeech.getTag().equalsIgnoreCase(posTag)
                            && (headTemplateName.equalsIgnoreCase(POSParserKey.HeadTemplateName)
                        || headTemplateName.equalsIgnoreCase(POSParserKey.partOfSpeech.name()))
                )
                .findFirst();
    }
}
