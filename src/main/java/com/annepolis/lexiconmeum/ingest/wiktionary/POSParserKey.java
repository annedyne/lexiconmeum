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

    /**
     * Attempts to find a {@link POSParserKey} corresponding to the given head template name.
     * Matches are determined by either the {@code HeadTemplateName} or the {@code partOfSpeech} name
     * in a case-insensitive manner.
     *
     * @param headTemplateName the name of the head template to search for; cannot be null or empty
     * @return an {@code Optional} containing the matched {@code POSParserKey} if found,
     *         or an empty {@code Optional} if no match is found
     */
    public static Optional<POSParserKey> fromHeadTemplateName(String headTemplateName) {
        return Arrays.stream(values())
                .filter(POSParserKey ->
                        headTemplateName.equalsIgnoreCase(POSParserKey.HeadTemplateName)
                        || headTemplateName.equalsIgnoreCase(POSParserKey.partOfSpeech.name())
                )
                .findFirst();
    }
}
