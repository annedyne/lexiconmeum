package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPerson;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void genderedCompoundFormsSupersedeUngenderedBaseline(){
        DataLinkingService underTest = new DataLinkingService(
                new CompoundInflectionGenerator(new EsseFormProvider()));

        String parentLemma = "amo";
        String parentLemmaWithMacrons = "amō";

        // Seed the parent with the parse-time baseline, which reuses the singular base for plurals.
        LexemeBuilder lexemeBuilder = new LexemeBuilder(parentLemma, PartOfSpeech.VERB, "1");
        lexemeBuilder.addInflection(new Conjugation.Builder("amātus sumus")
                .setVoice(GrammaticalVoice.PASSIVE)
                .setMood(GrammaticalMood.INDICATIVE)
                .setTense(GrammaticalTense.PERFECT)
                .setPerson(GrammaticalPerson.FIRST)
                .setNumber(GrammaticalNumber.PLURAL)
                .build());
        Lexeme parentLexeme = lexemeBuilder.build();
        StagedLexemeCache stagedLexemeCache = new StagedLexemeCache();
        stagedLexemeCache.putLexeme(parentLexeme);

        ParticipleDeclensionSet perfectPassiveSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT, "amātus")
                .addInflection(nominative("amātī", GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE))
                .build();

        underTest.stageDataToLink(new StagedParticipleData(parentLemma, parentLemmaWithMacrons, perfectPassiveSet));
        underTest.finalizeLexicalDataLinking(this::setCachedLexeme, stagedLexemeCache);

        Map<String, Inflection> inflections = cachedLexeme.getInflectionIndex();
        // Ungendered baseline removed; number-agreeing gendered form takes its place.
        assertNull(inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL"));
        assertEquals("amātī sumus",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL|MASCULINE").getForm());
    }

    @Test
    void pronounPluralFormsMergeIntoParent(){
        DataLinkingService underTest = new DataLinkingService(
                new CompoundInflectionGenerator(new EsseFormProvider()));

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
