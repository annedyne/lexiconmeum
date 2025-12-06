package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConjugationTableMapper {

    // Comparator orders individual tense groups by natural enum order
    Comparator<ConjugationTableDTO> conjugationTableDTOComparator =
        Comparator.comparing(
                (ConjugationTableDTO dto) -> GrammaticalVoice.valueOf(dto.getVoice()),
                Comparator.nullsLast(Comparator.naturalOrder())
        )
        .thenComparing(
                dto -> GrammaticalMood.valueOf(dto.getMood().toUpperCase()),
                Comparator.nullsLast(Comparator.naturalOrder())
        );

    record MoodVoiceKey(GrammaticalMood mood, GrammaticalVoice voice) {}


    public List<ConjugationTableDTO> toInflectionTableDTO(Lexeme lexeme) {

        // Process and extract conjugations from Lexeme
        List<Conjugation> conjugations = extractConjugations(lexeme);

        // Group my Mood and Voice
        Map<MoodVoiceKey, List<Conjugation>> conjugationsByMoodAndVoice = groupConjugationsByMoodAndVoice(conjugations);

        // Create and populate the DTOs
        List<ConjugationTableDTO> tableDTOs = generateDTOs(conjugationsByMoodAndVoice);

        // Sort tense groups by voice then mood
        List<ConjugationTableDTO> sorted = tableDTOs.stream()
                .sorted(conjugationTableDTOComparator)
                .toList();

        return sorted;
    }

    List<Conjugation> extractConjugations(Lexeme lexeme){
        List<Conjugation> conjugations = lexeme.getInflections().stream()
                .filter(Conjugation.class::isInstance)
                .map(Conjugation.class::cast)
                .toList();

        if (conjugations.isEmpty()) {
            throw new IllegalArgumentException("Lexeme contains no conjugation forms.");
        }
        return conjugations;
    }

    Map<MoodVoiceKey, List<Conjugation>> groupConjugationsByMoodAndVoice(List<Conjugation> conjugations){
        return conjugations.stream()
                .filter(c -> c.getMood() != null && c.getVoice() != null)
                .collect(Collectors.groupingBy(
                        c -> new MoodVoiceKey(c.getMood(), c.getVoice())
                ));
    }

    List<ConjugationTableDTO> generateDTOs(Map<MoodVoiceKey, List<Conjugation>> byMoodAndVoiceList){
        List<ConjugationTableDTO> tableDTOs = new ArrayList<>();

        for (Map.Entry<MoodVoiceKey, List<Conjugation>> moodAndVoiceEntry : byMoodAndVoiceList.entrySet()) {

            Map<GrammaticalTense, List<String>> byTense = groupByTense(moodAndVoiceEntry.getValue());
            List<ConjugationTableDTO.TenseDTO> tenseDTOs = createTenseDTOs(byTense);

            MoodVoiceKey moodAndVoiceInfo = moodAndVoiceEntry.getKey();
            tableDTOs.add(createConjugationDTO(moodAndVoiceInfo, tenseDTOs));
        }
        return tableDTOs;
    }

    // Map forms for each tense to their respective GrammaticalTense in enum order
    Map<GrammaticalTense, List<String>> groupByTense(List<Conjugation> groupConjugations) {
        return groupConjugations.stream()
                .sorted(Comparator.comparing(Conjugation::getNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Conjugation::getPerson, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.groupingBy(
                        Conjugation::getTense,
                        // TreeMap keeps order of GrammaticalTense enum
                        TreeMap::new,
                        Collectors.mapping(Conjugation::getForm, Collectors.toList())
                ));
    }

    List<ConjugationTableDTO.TenseDTO> createTenseDTOs(Map<GrammaticalTense, List<String>> inflectionsByTense){
       return inflectionsByTense.entrySet().stream()
                .map(e -> {
                    ConjugationTableDTO.TenseDTO dto = new ConjugationTableDTO.TenseDTO();
                    dto.setDefaultName(e.getKey().getHistoricalName());
                    dto.setAltName(e.getKey().getAlternativeName());
                    dto.setForms(e.getValue());
                    return dto;
                })
                .toList();
    }

    ConjugationTableDTO createConjugationDTO(MoodVoiceKey moodAndVoiceInfo, List<ConjugationTableDTO.TenseDTO> tenseDTOs){
        ConjugationTableDTO dto = new ConjugationTableDTO();
        dto.setMood(moodAndVoiceInfo.mood().getHistoricalName());
        dto.setVoice(moodAndVoiceInfo.voice().name());
        dto.setTenses(tenseDTOs);
        return dto;
    }
}
