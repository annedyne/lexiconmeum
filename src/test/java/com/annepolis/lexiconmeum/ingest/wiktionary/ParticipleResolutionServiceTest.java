package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipleResolutionServiceTest {
    public Lexeme cachedLexeme;



    @Test
    void allStagedParticipleSetsAreSuccessfullyAddedToLexeme(){
        // Testing bug-fix where parent lexeme candidates list
        // was not being refreshed after a participle was added

        // Instantiate Class under test.
        ParticipleResolutionService underTest = new ParticipleResolutionService();

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
                ).build());

        StagedParticipleData presentActive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE,
                        GrammaticalTense.PRESENT,
                        "amans"
                ).build());

        StagedParticipleData futureActive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE,
                        GrammaticalTense.FUTURE,
                        "amaturus"
                ).build());

        StagedParticipleData gerundive = new StagedParticipleData(
                parentLemma,
                parentLemmaWithMacrons,
                new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE,
                        GrammaticalTense.FUTURE,
                        "amandus"
                ).build());

        // Stage the two test participles.
        underTest.stageParticiple(perfectPassive);
        underTest.stageParticiple(presentActive);
        underTest.stageParticiple(futureActive);
        underTest.stageParticiple(gerundive);

        // Call finalize which contains the functionality under test
        underTest.finalizeParticiples(this::setCachedLexeme, stagedLexemeCache);

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
