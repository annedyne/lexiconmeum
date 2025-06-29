package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalMood;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalVoice;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LexemeConjugationMapper implements LexemeInflectionMapper {

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

        List<Conjugation> conjugations = lexeme.getInflections().stream()
                .filter(Conjugation.class::isInstance)
                .map(Conjugation.class::cast)
                .toList();

        if (conjugations.isEmpty()) {
            throw new IllegalArgumentException("Lexeme contains no conjugation forms.");
        }

        //Group forms by mood and voice tag
        Map<MoodVoiceKey, List<Conjugation>> grouped = conjugations.stream()
                .collect(Collectors.groupingBy(
                        c -> new MoodVoiceKey(c.getMood(), c.getVoice())
                ));

        List<ConjugationTableDTO> result = new ArrayList<>();

        //Within the same Voice and Mood, group forms by tense tag
        for (Map.Entry<MoodVoiceKey, List<Conjugation>> entry : grouped.entrySet()) {
            MoodVoiceKey key = entry.getKey();
            List<Conjugation> groupConjugations = entry.getValue();

            Map<GrammaticalTense, List<String>> byTense = groupConjugations.stream()
                    .collect(Collectors.groupingBy(
                            Conjugation::getTense,
                            TreeMap::new,
                            Collectors.mapping(Conjugation::getForm, Collectors.toList())
                    ));

            //Map other tense info into tense DTO
            List<ConjugationTableDTO.TenseDTO> tenseDTOs = byTense.entrySet().stream()
                    .map(e -> {
                        ConjugationTableDTO.TenseDTO dto = new ConjugationTableDTO.TenseDTO();
                        dto.setDefaultName(e.getKey().getHistoricalName());
                        dto.setAltName(e.getKey().getAlternativeName());
                        dto.setForms(e.getValue());
                        return dto;
                    })
                    .toList();

            ConjugationTableDTO dto = new ConjugationTableDTO();
            dto.setMood(key.mood().getHistoricalName());
            dto.setVoice(key.voice().name());
            dto.setTenses(tenseDTOs);

            result.add(dto);
        }

        List<ConjugationTableDTO> sorted = result.stream()
                .sorted(conjugationTableDTOComparator)
                .toList();

        return new ConjugationGroupDTO(sorted);
    }

}
