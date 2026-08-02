package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;

import java.util.List;

/**
 * Holds a suppletive plural personal-pronoun form (e.g. 'nos', 'vos')
 * pending linking to its singular parent pronoun (e.g. 'ego', 'tu').
 */
public class StagedPronounData implements LinkableData {

    private final String childLemma;
    private final String parentLemma;
    private final List<Inflection> childInflections;

    public StagedPronounData(String childLemma, String parentLemma, List<Inflection> childInflections) {
        this.childLemma = childLemma;
        this.parentLemma = parentLemma;
        this.childInflections = childInflections;
    }

    @Override
    public String getLemma() {
        return childLemma;
    }

    @Override
    public String getLinkingLemma() {
        return parentLemma;
    }

    @Override
    public Lexeme link(Lexeme parent) {
        // Add the child's plural agreements onto the parent. Parent forms are singular,
        // so keys differ by number and no existing parent form is overwritten.
        LexemeBuilder builder = LexemeBuilder.fromLexeme(parent);
        childInflections.forEach(builder::addInflection);
        return builder.build();
    }

    @Override
    public PartOfSpeech getParentLinkPartOfSpeech() {
        return PartOfSpeech.PRONOUN;
    }
}
