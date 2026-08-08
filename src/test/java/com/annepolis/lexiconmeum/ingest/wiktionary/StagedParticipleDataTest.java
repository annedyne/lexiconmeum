package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.EsseFormProvider;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.MorphologicalSubtype;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers the compound perfect-system forms a participle set projects onto its
 * parent verb when it links.
 */
class StagedParticipleDataTest {

    private static final CompoundInflectionGenerator COMPOUND_GENERATOR =
            new CompoundInflectionGenerator(new EsseFormProvider());

    @Test
    void perfectPassiveParticipleAddsGenderedCompoundForms() {
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

        Lexeme amo = new LexemeBuilder("amo", PartOfSpeech.VERB, "1").build();

        Lexeme result = stagedParticiple("amo", "amō", perfectPassiveSet).link(amo);

        Map<String, Inflection> inflections = result.getInflectionIndex();
        assertEquals("amāta sum",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|FEMININE").getForm());
        assertEquals("amātum sum",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|NEUTER").getForm());
        assertEquals("amātae sumus",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL|FEMININE").getForm());
    }

    @Test
    void genderedCompoundFormsSupersedeUngenderedBaseline() {
        // Seed the parent with the parse-time baseline, which reuses the singular base for plurals.
        LexemeBuilder lexemeBuilder = new LexemeBuilder("amo", PartOfSpeech.VERB, "1");
        lexemeBuilder.addInflection(new Conjugation.Builder("amātus sumus")
                .setVoice(GrammaticalVoice.PASSIVE)
                .setMood(GrammaticalMood.INDICATIVE)
                .setTense(GrammaticalTense.PERFECT)
                .setPerson(GrammaticalPerson.FIRST)
                .setNumber(GrammaticalNumber.PLURAL)
                .build());
        Lexeme amo = lexemeBuilder.build();

        ParticipleDeclensionSet perfectPassiveSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT, "amātus")
                .addInflection(nominative("amātī", GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE))
                .build();

        Lexeme result = stagedParticiple("amo", "amō", perfectPassiveSet).link(amo);

        Map<String, Inflection> inflections = result.getInflectionIndex();
        // Ungendered baseline removed; number-agreeing gendered form takes its place.
        assertNull(inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL"));
        assertEquals("amātī sumus",
                inflections.get("PASSIVE|INDICATIVE|PERFECT|FIRST|PLURAL|MASCULINE").getForm());
    }

    @Test
    void perfectActiveParticipleSkipsGenderedFormsForNonDeponentVerb() {
        // Semi-deponent verbs (e.g. placeo) have a real synthetic active perfect
        // (placui) plus a participle-based periphrastic alternative (placitus sum)
        // that Wiktionary also tags 'active'. Since placeo is not DEPONENT, no
        // standalone gendered ACTIVE entry should be generated from the participle.
        LexemeBuilder lexemeBuilder = new LexemeBuilder("placeo", PartOfSpeech.VERB, "1");
        lexemeBuilder.addInflection(new Conjugation.Builder("placuī")
                .setVoice(GrammaticalVoice.ACTIVE)
                .setMood(GrammaticalMood.INDICATIVE)
                .setTense(GrammaticalTense.PERFECT)
                .setPerson(GrammaticalPerson.FIRST)
                .setNumber(GrammaticalNumber.SINGULAR)
                .build());
        Lexeme placeo = lexemeBuilder.build();

        ParticipleDeclensionSet perfectActiveSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE, GrammaticalTense.PERFECT, "placitus")
                .addInflection(nominative("placitus", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE))
                .build();

        Lexeme result = stagedParticiple("placeo", "placeō", perfectActiveSet).link(placeo);

        Map<String, Inflection> inflections = result.getInflectionIndex();
        // Synthetic baseline untouched, no standalone gendered entry generated.
        assertEquals("placuī", inflections.get("ACTIVE|INDICATIVE|PERFECT|FIRST|SINGULAR").getForm());
        assertNull(inflections.get("ACTIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|MASCULINE"));
    }

    @Test
    void perfectActiveParticipleAddsGenderedFormsForDeponentVerb() {
        // A true deponent's perfect system has no separate synthetic active form -
        // the periphrastic, gendered form is the only paradigm, so it should be generated.
        LexemeBuilder lexemeBuilder = new LexemeBuilder("hortor", PartOfSpeech.VERB, "1");
        lexemeBuilder.setPartOfSpeechDetails(new VerbDetails.Builder()
                .setMorphologicalSubtype(MorphologicalSubtype.DEPONENT)
                .build());
        Lexeme hortor = lexemeBuilder.build();

        ParticipleDeclensionSet perfectActiveSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE, GrammaticalTense.PERFECT, "hortātus")
                .addInflection(nominative("hortātus", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE))
                .build();

        Lexeme result = stagedParticiple("hortor", "hortor", perfectActiveSet).link(hortor);

        assertEquals("hortātus sum",
                result.getInflectionIndex().get("ACTIVE|INDICATIVE|PERFECT|FIRST|SINGULAR|MASCULINE").getForm());
    }

    @Test
    void nonPerfectParticipleAddsNoCompoundForms() {
        Lexeme amo = new LexemeBuilder("amo", PartOfSpeech.VERB, "1").build();

        ParticipleDeclensionSet presentActiveSet = new ParticipleDeclensionSet.Builder(
                        GrammaticalVoice.ACTIVE, GrammaticalTense.PRESENT, "amāns")
                .addInflection(nominative("amāns", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE))
                .build();

        Lexeme result = stagedParticiple("amo", "amō", presentActiveSet).link(amo);

        assertTrue(result.getInflections().isEmpty(), "present participle should add no compound forms");
    }

    // The participle set is still attached to the verb regardless of whether it
    // contributes compound forms.
    @Test
    void participleSetIsAttachedToVerbDetails() {
        Lexeme amo = new LexemeBuilder("amo", PartOfSpeech.VERB, "1").build();

        ParticipleDeclensionSet perfectPassiveSet = new ParticipleDeclensionSet.Builder(
                GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT, "amātus").build();

        Lexeme result = stagedParticiple("amo", "amō", perfectPassiveSet).link(amo);

        VerbDetails verbDetails = (VerbDetails) result.getPartOfSpeechDetails();
        assertTrue(verbDetails.getParticipleSet(GrammaticalVoice.PASSIVE, GrammaticalTense.PERFECT).isPresent());
    }

    private static StagedParticipleData stagedParticiple(
            String parentLemma, String parentLemmaWithMacrons, ParticipleDeclensionSet participleSet) {
        return new StagedParticipleData(parentLemma, parentLemmaWithMacrons, participleSet, COMPOUND_GENERATOR);
    }

    private static Participle nominative(String form, GrammaticalNumber number, GrammaticalGender gender) {
        return new Participle.Builder(form)
                .setGrammaticalCase(GrammaticalCase.NOMINATIVE)
                .setNumber(number)
                .addGender(gender)
                .build();
    }
}