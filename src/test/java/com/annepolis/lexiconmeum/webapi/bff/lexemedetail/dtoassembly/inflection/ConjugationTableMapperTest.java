package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConjugationTableMapperTest {

    @Test
    void toConjugationTablesMoodsMappedInOrder(){
        ConjugationTableMapper mapper = new ConjugationTableMapper();
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        ConjugationGroupDTO groupDTO = mapper.toInflectionTableDTO(lexeme);
        assertEquals(GrammaticalMood.INDICATIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(0).getMood());
        assertEquals(GrammaticalMood.SUBJUNCTIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(1).getMood());
        assertEquals(GrammaticalMood.INFINITIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(2).getMood());
        assertEquals(GrammaticalMood.INDICATIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(3).getMood());
        assertEquals(GrammaticalVoice.PASSIVE.name(), groupDTO.getConjugationTableDTOList().get(3).getVoice());
    }

    @Test
    void forms_inNumberAndPerson_Ascending(){
        ConjugationTableMapper mapper = new ConjugationTableMapper();
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        ConjugationGroupDTO groupDTO = mapper.toInflectionTableDTO(lexeme);

        List<ConjugationTableDTO> tableDTOS =  groupDTO.getConjugationTableDTOList();
        assertEquals("amō", tableDTOS.get(0).getTenses().get(0).getForms().get(0));
        assertEquals("amās", tableDTOS.get(0).getTenses().get(0).getForms().get(1));
        assertEquals("amat", tableDTOS.get(0).getTenses().get(0).getForms().get(2));
        assertEquals("amāmus", tableDTOS.get(0).getTenses().get(0).getForms().get(3));
        assertEquals("amātis", tableDTOS.get(0).getTenses().get(0).getForms().get(4));
        assertEquals("amant", tableDTOS.get(0).getTenses().get(0).getForms().get(5));
    }

    @Test
    void toConjugationTableMapsAllFormsInGivenLexemeTense(){
        ConjugationTableMapper mapper = new ConjugationTableMapper();
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        ConjugationGroupDTO groupDTO = mapper.toInflectionTableDTO(lexeme);

        ConjugationTableDTO activeIndicative = groupDTO.getConjugationTableDTOList().stream()
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
}