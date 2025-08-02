package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
class LexemeConjugationMapper implements LexemeInflectionMapper {

    static Comparator<ConjugationTableDTO> conjugationTableDTOComparator =
            Comparator.comparing(LexemeConjugationMapper::resolveVoiceOrder)
                    .thenComparing(LexemeConjugationMapper::resolveMoodOrder);

    private static int resolveVoiceOrder(ConjugationTableDTO dto) {
        try {
            return GrammaticalVoice.valueOf(dto.getVoice()).ordinal();
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    private static int resolveMoodOrder(ConjugationTableDTO dto) {
        String mood = dto.getMood();
        if (mood == null) return Integer.MAX_VALUE;
        try {
            return GrammaticalMood.valueOf(mood.toUpperCase()).ordinal();
        } catch (IllegalArgumentException e) {
            return Integer.MAX_VALUE - 1;
        }
    }

    record MoodVoiceKey(GrammaticalMood mood, GrammaticalVoice voice) {}

    @Override
    public ConjugationGroupDTO toInflectionTableDTO(Lexeme lexeme) {

        List<Conjugation> conjugations = extractConjugations(lexeme);
        Map<MoodVoiceKey, List<Conjugation>> conjugationsByMoodAndVoice = groupConjugationsByMoodAndVoice(conjugations);

        List<ConjugationTableDTO> tableDTOs = generateDTOs(conjugationsByMoodAndVoice);

        List<ConjugationTableDTO> sorted = tableDTOs.stream()
                .sorted(conjugationTableDTOComparator)
                .toList();

        return new ConjugationGroupDTO(sorted);
    }

    List<ConjugationTableDTO> generateDTOs(Map<MoodVoiceKey, List<Conjugation>> byMoodAndVoiceList){
        List<ConjugationTableDTO> tableDTOs = new ArrayList<>();

        for (Map.Entry<MoodVoiceKey, List<Conjugation>> moodAndVoiceEntry : byMoodAndVoiceList.entrySet()) {

            Map<GrammaticalTense, List<String>> byTense = groupByTense(moodAndVoiceEntry.getValue());
            List<ConjugationTableDTO.TenseDTO> tenseDTOs = createTenseDTOs(byTense);

            MoodVoiceKey moodAndVoiceInfo = moodAndVoiceEntry.getKey();
            tableDTOs.add(populateNewConjugationDTO(moodAndVoiceInfo, tenseDTOs));
        }
        return tableDTOs;
    }

    ConjugationTableDTO populateNewConjugationDTO(MoodVoiceKey moodAndVoiceInfo, List<ConjugationTableDTO.TenseDTO> tenseDTOs){
        ConjugationTableDTO dto = new ConjugationTableDTO();
        dto.setMood(moodAndVoiceInfo.mood().getHistoricalName());
        dto.setVoice(moodAndVoiceInfo.voice().name());
        dto.setTenses(tenseDTOs);
        return dto;
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
               .collect(Collectors.groupingBy(
                       c -> new MoodVoiceKey(c.getMood(), c.getVoice())
               ));
   }

    Map<GrammaticalTense, List<String>> groupByTense(List<Conjugation> groupConjugations) {
        return groupConjugations.stream()
                .collect(Collectors.groupingBy(
                        Conjugation::getTense,
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
}
