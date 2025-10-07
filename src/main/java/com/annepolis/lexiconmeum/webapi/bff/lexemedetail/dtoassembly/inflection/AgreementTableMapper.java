package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeechDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AgreementTableMapper implements InflectionTableMapper {

    @Override
    public InflectionTableDTO toInflectionTableDTO(Lexeme lexeme) {
        Map<Set<GrammaticalGender>,
                Map<GrammaticalNumber, Map<GrammaticalCase, String>>> table = new LinkedHashMap<>();

        if (lexeme != null) {
            boolean twoTermination;
            PartOfSpeechDetails details = lexeme.getPartOfSpeechDetails();
            if (details instanceof AdjectiveDetails adj) {
                twoTermination = adj.terminationType() == AdjectiveTerminationType.TWO_TERMINATION;
            } else {
                twoTermination = false;
            }

            lexeme.getInflections().stream()
                    .filter(Agreement.class::isInstance)
                    .map(Agreement.class::cast)
                    .forEach(agreement -> {
                        Set<GrammaticalGender> genders = agreement.getGenders();
                        List<Set<GrammaticalGender>> genderGroups;

                        if (twoTermination && genders.containsAll(Set.of(
                                GrammaticalGender.MASCULINE,
                                GrammaticalGender.FEMININE,
                                GrammaticalGender.NEUTER))) {
                            // 3rd decl: split M+F vs N
                            genderGroups = List.of(
                                    Set.of(GrammaticalGender.MASCULINE, GrammaticalGender.FEMININE),
                                    Set.of(GrammaticalGender.NEUTER)
                            );
                        } else if (!twoTermination) {
                            // 1st/2nd: expand into singletons
                            genderGroups = genders.stream()
                                    .map(Set::of)
                                    .toList();
                        } else {
                            // otherwise, keep the full set
                            genderGroups = List.of(new TreeSet<>(genders));
                        }

                        for (Set<GrammaticalGender> genderSet : genderGroups) {
                            table
                                    .computeIfAbsent(genderSet, g -> new EnumMap<>(GrammaticalNumber.class))
                                    .computeIfAbsent(agreement.getNumber(),
                                            n -> new EnumMap<>(GrammaticalCase.class))
                                    .put(agreement.getGrammaticalCase(), agreement.getForm());
                        }
                    });
        }

        List<AgreementEntryDTO> entries = table.entrySet().stream()
                .map(e -> {
                    AgreementEntryDTO entry = new AgreementEntryDTO();
                    // insertion order in each list of genders being preserved
                    // but is that the order we want? At least it's consistent
                    entry.setGenders(new ArrayList<>(e.getKey()));
                    entry.setInflections(e.getValue());
                    return entry;
                })
                .toList();

        AgreementTableDTO dto = new AgreementTableDTO();
        dto.setAgreements(entries);
        return dto;
    }
}
