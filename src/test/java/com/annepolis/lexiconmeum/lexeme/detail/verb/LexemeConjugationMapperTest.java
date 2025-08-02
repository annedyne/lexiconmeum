package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeConjugationMapperTest {

    @Test
    void toConjugationTablesMappedInOrder(){
        LexemeConjugationMapper mapper = new LexemeConjugationMapper();
        Lexeme lexeme = TestUtil.getNewTestVerbLexeme();
        ConjugationGroupDTO groupDTO = mapper.toInflectionTableDTO(lexeme);
        assertEquals(GrammaticalMood.INDICATIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(0).getMood());
        assertEquals(GrammaticalMood.SUBJUNCTIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(1).getMood());
        assertEquals(GrammaticalMood.INFINITIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(2).getMood());
        assertEquals(GrammaticalMood.INDICATIVE.getHistoricalName(), groupDTO.getConjugationTableDTOList().get(3).getMood());
        assertEquals(GrammaticalVoice.PASSIVE.name(), groupDTO.getConjugationTableDTOList().get(3).getVoice());
    }
    @Test
    void toConjugationTableMapsAllFormsInGivenLexemeTense(){
        LexemeConjugationMapper mapper = new LexemeConjugationMapper();
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