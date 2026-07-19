package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPerson;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConjugationTableMapperTest {

    @Test
    void toConjugationTablesMoodsMappedInOrder() throws IOException {
        ConjugationTableMapper mapper = new ConjugationTableMapper();
        Lexeme lexeme = JsonTestDataManager.INSTANCE.getParsedVerbLexeme("amo", "testDataVerb.jsonl");
        List<ConjugationTableDTO> conjugationTableDTOS = mapper.toInflectionTableDTO(lexeme);
        assertEquals(GrammaticalMood.INDICATIVE.getHistoricalName(), conjugationTableDTOS.get(0).getMood());
        assertEquals(GrammaticalMood.SUBJUNCTIVE.getHistoricalName(), conjugationTableDTOS.get(1).getMood());
        assertEquals(GrammaticalMood.INFINITIVE.getHistoricalName(), conjugationTableDTOS.get(2).getMood());
        assertEquals(GrammaticalMood.IMPERATIVE.getHistoricalName(), conjugationTableDTOS.get(3).getMood());
        assertEquals(GrammaticalVoice.PASSIVE.name(), conjugationTableDTOS.get(4).getVoice());
    }

    @Test
    void forms_inNumberAndPerson_Ascending() throws IOException {
        ConjugationTableMapper mapper = new ConjugationTableMapper();
        Lexeme lexeme = JsonTestDataManager.INSTANCE.getParsedVerbLexeme("amo", "testDataVerb.jsonl");

        List<ConjugationTableDTO> tableDTOS = mapper.toInflectionTableDTO(lexeme);
        assertEquals("amō", tableDTOS.get(0).getTenses().get(0).getForms().get(0));
        assertEquals("amās", tableDTOS.get(0).getTenses().get(0).getForms().get(1));
        assertEquals("amat", tableDTOS.get(0).getTenses().get(0).getForms().get(2));
        assertEquals("amāmus", tableDTOS.get(0).getTenses().get(0).getForms().get(3));
        assertEquals("amātis", tableDTOS.get(0).getTenses().get(0).getForms().get(4));
        assertEquals("amant", tableDTOS.get(0).getTenses().get(0).getForms().get(5));
    }

    @Test
    void toConjugationTableMapsAllFormsInGivenLexemeTense() throws IOException {
        ConjugationTableMapper mapper = new ConjugationTableMapper();
        Lexeme lexeme = JsonTestDataManager.INSTANCE.getParsedVerbLexeme("amo", "testDataVerb.jsonl");
        List<ConjugationTableDTO> tableDTOS = mapper.toInflectionTableDTO(lexeme);

        ConjugationTableDTO activeIndicative = tableDTOS.stream()
                .filter(g -> g.getVoice().equals("ACTIVE") && g.getMood().equals("Indicative"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing ACTIVE/Indicative group"));

        ConjugationTableDTO.TenseDTO present = activeIndicative.getTenses().stream()
                .filter(g -> g.getDefaultName().equalsIgnoreCase("present"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing present-tense forms"));

        int actualSize = present.getForms().size();
        assertEquals(6, actualSize, "Should have 6 present forms");
    }

    @Test
    void genderedPerfectPassiveFormsSurfaceInGenderOrder() {
        ConjugationTableMapper mapper = new ConjugationTableMapper();

        // Insert scrambled to prove the mapper imposes the order, not insertion.
        LexemeBuilder builder = new LexemeBuilder("amo", PartOfSpeech.VERB, "1");
        builder.addInflection(perfectPassive("amātum sum", GrammaticalNumber.SINGULAR, GrammaticalGender.NEUTER));
        builder.addInflection(perfectPassive("amātus sum", GrammaticalNumber.SINGULAR, GrammaticalGender.MASCULINE));
        builder.addInflection(perfectPassive("amāta sum", GrammaticalNumber.SINGULAR, GrammaticalGender.FEMININE));
        Lexeme lexeme = builder.build();

        ConjugationTableDTO passiveIndicative = mapper.toInflectionTableDTO(lexeme).stream()
                .filter(g -> g.getVoice().equals("PASSIVE") && g.getMood().equals("Indicative"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing PASSIVE/Indicative group"));

        ConjugationTableDTO.TenseDTO perfect = passiveIndicative.getTenses().stream()
                .filter(t -> t.getDefaultName().equalsIgnoreCase("perfect"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing perfect-tense forms"));

        // Masculine, feminine, neuter within the same person/number cell.
        assertEquals(List.of("amātus sum", "amāta sum", "amātum sum"), perfect.getForms());
    }

    private static Conjugation perfectPassive(String form, GrammaticalNumber number, GrammaticalGender gender) {
        return new Conjugation.Builder(form)
                .setVoice(GrammaticalVoice.PASSIVE)
                .setMood(GrammaticalMood.INDICATIVE)
                .setTense(GrammaticalTense.PERFECT)
                .setPerson(GrammaticalPerson.FIRST)
                .setNumber(number)
                .setGender(gender)
                .build();
    }
}