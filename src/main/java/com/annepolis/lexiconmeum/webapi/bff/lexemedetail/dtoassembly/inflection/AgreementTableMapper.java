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

    AgreementTableDTO toInflectionTableDTO(List<Inflection> inflections,  boolean twoTermination) {
       return generateDTO(inflections, twoTermination);
    }

    private AgreementTableDTO generateDTO(List<Inflection> inflections,  boolean twoTermination) {
        Map<Set<GrammaticalGender>,
                Map<GrammaticalNumber, Map<GrammaticalCase, String>>> table = new LinkedHashMap<>();

        generateInflectionTable(inflections, twoTermination, table);

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
            boolean twoTermination,
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
                        table.computeIfAbsent(genderSet, g -> new EnumMap<>(GrammaticalNumber.class))
                                .computeIfAbsent(agreement.getNumber(),
                                        n -> new EnumMap<>(GrammaticalCase.class))
                                .put(agreement.getGrammaticalCase(), agreement.getForm());
                    }
                });
    }
}
