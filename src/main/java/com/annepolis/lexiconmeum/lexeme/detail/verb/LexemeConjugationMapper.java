package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class LexemeConjugationMapper implements LexemeInflectionMapper {


    @Override
    public ConjugationTableDTO toInflectionTableDTO(Lexeme lexeme) {

        List<Conjugation> conjugations = lexeme.getInflections().stream()
                .filter(Conjugation.class::isInstance)
                .map(Conjugation.class::cast)
                .toList();


        if (conjugations.isEmpty()) {
            throw new IllegalArgumentException("Lexeme contains no conjugation forms.");
        }

        // Assume all conjugations in the lexeme share the same mood and voice
        // Assume all conjugations in the lexeme share the same mood and voice
        GrammaticalMood mood = conjugations.get(0).getMood();
        GrammaticalVoice voice = conjugations.get(0).getVoice(); // assuming added

        Map<GrammaticalTense, List<String>> groupedByTense = conjugations.stream()
                .collect(Collectors.groupingBy(
                        Conjugation::getTense,
                        TreeMap::new, // maintain tense order if enum is ordered
                        Collectors.mapping(Conjugation::getForm, Collectors.toList())
                ));

        List<ConjugationTableDTO.TenseDTO> tenseDTOs = new ArrayList<>();
        for (Map.Entry<GrammaticalTense, List<String>> entry : groupedByTense.entrySet()) {
            GrammaticalTense tense = entry.getKey();
            List<String> forms = entry.getValue();

            ConjugationTableDTO conjugationTableDTO = new ConjugationTableDTO();
            ConjugationTableDTO.TenseDTO tenseDTO = conjugationTableDTO.new TenseDTO();

            tenseDTO.setDefaultName(tense.getHistoricalName());
            tenseDTO.setAltName(tense.getAlternativeName()); // if available
            tenseDTO.setForms(forms);

            tenseDTOs.add(tenseDTO);
        }

        ConjugationTableDTO dto = new ConjugationTableDTO();
        dto.setMood(mood.getHistoricalName());
        dto.setVoice(voice.name());
        dto.setTenses(tenseDTOs);

        return dto;
    }

}
