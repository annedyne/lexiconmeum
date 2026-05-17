package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AgreementTableMapper  implements InflectionTableMapper {

    @Override
    public AgreementTableDTO toInflectionTableDTO(Lexeme lexeme) {

        return  generateDTO(lexeme.getInflections(), false);
    }

    AgreementTableDTO toInflectionTableDTO(List<Inflection> inflections,  boolean twoOrOneTermination) {
       return generateDTO(inflections, twoOrOneTermination);
    }

    private AgreementTableDTO generateDTO(List<Inflection> inflections,  boolean twoOrOneTermination) {
        Map<Set<GrammaticalGender>,
                Map<GrammaticalNumber, Map<GrammaticalCase, String>>> table = new LinkedHashMap<>();

        generateInflectionTable(inflections, twoOrOneTermination, table);

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

    private static void generateInflectionTable(
            List<Inflection> inflections,
            boolean twoOrOneTermination,
            Map<Set<GrammaticalGender>, Map<GrammaticalNumber, Map<GrammaticalCase, String>>> table
    ) {
        if (inflections == null || inflections.isEmpty()) {
            return;
        }

        inflections.stream()
                .filter(Agreement.class::isInstance)
                .map(Agreement.class::cast)
                .forEach(agreement -> {
                    Set<GrammaticalGender> genders = agreement.getGenders();
                    List<Set<GrammaticalGender>> genderGroups;
                    // If all three genders are present, but adjective is not three-termination --
                    if (twoOrOneTermination && genders.containsAll(Set.of(
                            GrammaticalGender.MASCULINE,
                            GrammaticalGender.FEMININE,
                            GrammaticalGender.NEUTER))) {
                        // Merge masculine and feminine into one gender group.
                        genderGroups = List.of(
                                Set.of(GrammaticalGender.MASCULINE, GrammaticalGender.FEMININE),
                                Set.of(GrammaticalGender.NEUTER)
                        );
                    } else if (!twoOrOneTermination) {
                        // if it's a three termination (1st or 2nd declension)
                        //
                        genderGroups = genders.stream()
                                .map(Set::of)
                                .toList();
                    } else {
                        // otherwise, just use sets as they are in the data
                        genderGroups = List.of(new TreeSet<>(genders));
                    }

                    for (Set<GrammaticalGender> genderSet : genderGroups) {
                        table.computeIfAbsent(genderSet, g -> new EnumMap<>(GrammaticalNumber.class))
                                .computeIfAbsent(agreement.getNumber(),
                                        n -> new EnumMap<>(GrammaticalCase.class))
                                .put(agreement.getGrammaticalCase(), agreement.getForm());
                    }
                });
    }
}
