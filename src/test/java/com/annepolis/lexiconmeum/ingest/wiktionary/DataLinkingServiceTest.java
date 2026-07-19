package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataLinkingServiceTest {
    public Lexeme cachedLexeme;



    @Test
    void allStagedParticipleSetsAreSuccessfullyAddedToLexeme(){
        // Testing bug-fix where parent lexeme candidates list
        // was not being refreshed after a participle was added

        // Instantiate Class under test.
        DataLinkingService underTest = new DataLinkingService(
                new CompoundInflectionGenerator(new EsseFormProvider()));

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
    void genderedCompoundFormsAreAddedAfterLinking(){
        DataLinkingService underTest = new DataLinkingService(
                new CompoundInflectionGenerator(new EsseFormProvider()));

        String parentLemma = "amo";
        String parentLemmaWithMacrons = "amō";

        LexemeBuilder lexemeBuilder = new LexemeBuilder(parentLemma, PartOfSpeech.VERB, "1");
        Lexeme parentLexeme = lexemeBuilder.build();
        StagedLexemeCache stagedLexemeCache = new StagedLexemeCache();
        stagedLexemeCache.putLexeme(parentLexeme);

        // Perfect passive participle set with the gendered nominatives needed for compounds.
        ParticipleDeclensionSet perfectPassiveSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT, "amātus")
                .addInflection(nominative("amātus", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE))
                .addInflection(nominative("amāta", GrammaticalNumber.SINGULAR, GrammaticalGender.FEMININE))
                .addInflection(nominative("amātum", GrammaticalNumber.SINGULAR, GrammaticalGender.NEUTER))
                .addInflection(nominative("amātī", GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE))
                .addInflection(nominative("amātae", GrammaticalNumber.PLURAL, GrammaticalGender.FEMININE))
                .addInflection(nominative("amāta", GrammaticalNumber.PLURAL, GrammaticalGender.NEUTER))
                .build();

        underTest.stageDataToLink(new StagedParticipleData(parentLemma, parentLemmaWithMacrons, perfectPassiveSet));
        underTest.finalizeLexicalDataLinking(this::setCachedLexeme, stagedLexemeCache);

        Map<String, Inflection> inflections = cachedLexeme.getInflectionIndex();
        assertEquals("amāta sum",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|FEMININE").getForm());
        assertEquals("amātum sum",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|NEUTER").getForm());
        assertEquals("amātae sumus",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL|FEMININE").getForm());
    }

    private static Participle nominative(String form, GrammaticalNumber number, GrammaticalGender gender) {
        return new Participle.Builder(form)
                .setGrammaticalCase(GrammaticalCase.NOMINATIVE)
                .setNumber(number)
                .addGender(gender)
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
