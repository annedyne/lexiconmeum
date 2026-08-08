package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataLinkingServiceTest {
    public Lexeme cachedLexeme;

    private static final CompoundInflectionGenerator COMPOUND_GENERATOR =
            new CompoundInflectionGenerator(new EsseFormProvider());

    @Test
    void allStagedParticipleSetsAreSuccessfullyAddedToLexeme(){
        // Testing bug-fix where parent lexeme candidates list
        // was not being refreshed after a participle was added

        // Instantiate Class under test.
        DataLinkingService underTest = new DataLinkingService();

        // Set up parent Lexeme lemmas.
        String parentLemma = "amo";
        String parentLemmaWithMacrons = "amō";

        // Create Parent Lexeme.
        LexemeBuilder lexemeBuilder = new LexemeBuilder(parentLemma, PartOfSpeech.VERB, "1");
        Lexeme parentLexeme = lexemeBuilder.build();
        StagedLexemeCache stagedLexemeCache = new StagedLexemeCache();
        stagedLexemeCache.putLexeme(parentLexeme);

        StagedParticipleData perfectPassive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE,
                        GrammaticalTense.PERFECT,
                        "amata"
                ).build(),
                COMPOUND_GENERATOR);

        StagedParticipleData presentActive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE,
                        GrammaticalTense.PRESENT,
                        "amans"
                ).build(),
                COMPOUND_GENERATOR);

        StagedParticipleData futureActive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE,
                        GrammaticalTense.FUTURE,
                        "amaturus"
                ).build(),
                COMPOUND_GENERATOR);

        StagedParticipleData gerundive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE,
                        GrammaticalTense.FUTURE,
                        "amandus"
                ).build(),
                COMPOUND_GENERATOR);

        // Stage the two test participles.
        underTest.stageDataToLink(perfectPassive);
        underTest.stageDataToLink(presentActive);
        underTest.stageDataToLink(futureActive);
        underTest.stageDataToLink(gerundive);

        // Call finalize which contains the functionality under test
        underTest.finalizeLexicalDataLinking(this::setCachedLexeme, stagedLexemeCache);

        if(cachedLexeme.getPartOfSpeechDetails() instanceof VerbDetails verbDetails) {
            verbDetails.getParticipleSet(GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT)
                    .orElseThrow(() -> new AssertionError("Present Active Participle not found") );

            verbDetails.getParticipleSet(GrammaticalVoice.ACTIVE, GrammaticalTense.PRESENT)
                    .orElseThrow(() -> new AssertionError("Present Active Participle not found") );

            verbDetails.getParticipleSet(GrammaticalVoice.ACTIVE, GrammaticalTense.FUTURE)
                    .orElseThrow(() -> new AssertionError("Future Active Participle not found"));

            verbDetails.getParticipleSet(GrammaticalVoice.PASSIVE, GrammaticalTense.FUTURE)
                    .orElseThrow(() -> new AssertionError("Gerundive Participle not found"));

        } else {
            throw new AssertionError("No participles found");
        }

       assertEquals(4, getNumParticiples());
    }

    @Test
    void pronounPluralFormsMergeIntoParent(){
        DataLinkingService underTest = new DataLinkingService();

        // Parent 'ego' seeded with a genderless singular form.
        LexemeBuilder egoBuilder = new LexemeBuilder("ego", PartOfSpeech.PRONOUN, "1");
        egoBuilder.addInflection(
                genderlessAgreement("egō̆", GrammaticalCase.NOMINATIVE, GrammaticalNumber.SINGULAR));
        Lexeme ego = egoBuilder.build();

        StagedLexemeCache stagedLexemeCache = new StagedLexemeCache();
        stagedLexemeCache.putLexeme(ego);

        // Child 'nos' plural agreements staged for linking onto 'ego'.
        List<Inflection> nosPluralForms = List.of(
                genderlessAgreement("nōs", GrammaticalCase.NOMINATIVE, GrammaticalNumber.PLURAL),
                genderlessAgreement("nōbīs", GrammaticalCase.DATIVE, GrammaticalNumber.PLURAL));
        underTest.stageDataToLink(new StagedPronounData("nos", "ego", nosPluralForms));

        underTest.finalizeLexicalDataLinking(this::setCachedLexeme, stagedLexemeCache);

        List<Inflection> inflections = cachedLexeme.getInflections();
        // Parent singular retained; both child plural forms merged.
        assertEquals(3, inflections.size());
        assertTrue(inflections.stream().anyMatch(i -> i.getForm().equals("egō̆")), "singular form lost");
        assertTrue(inflections.stream().anyMatch(i -> i.getForm().equals("nōbīs")), "plural dative not merged");

        Agreement mergedNominative = (Agreement) inflections.stream()
                .filter(i -> i.getForm().equals("nōs"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("plural nominative not merged"));
        assertEquals(GrammaticalNumber.PLURAL, mergedNominative.getNumber());
    }

    private static Agreement genderlessAgreement(String form, GrammaticalCase grammaticalCase, GrammaticalNumber number) {
        return new Agreement.Builder(form)
                .setGrammaticalCase(grammaticalCase)
                .setNumber(number)
                .build();
    }

    int getNumParticiples(){
        if(cachedLexeme.getPartOfSpeechDetails() instanceof VerbDetails verbDetails) {
            return verbDetails.getParticiples().size();
        }
        return -1;
    }

    void setCachedLexeme(Lexeme cachedLexeme){
        this.cachedLexeme = cachedLexeme;
    }

}
