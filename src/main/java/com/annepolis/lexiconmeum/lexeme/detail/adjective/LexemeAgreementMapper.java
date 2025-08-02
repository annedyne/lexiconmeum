package com.annepolis.lexiconmeum.lexeme.detail.adjective;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LexemeAgreementMapper implements LexemeInflectionMapper {

    @Override
    public AgreementTableDTO toInflectionTableDTO(Lexeme lexeme) {
        Map<Set<GrammaticalGender>, Map<GrammaticalNumber, Map<GrammaticalCase, String>>> table = new LinkedHashMap<>();

        if (lexeme != null) {
            boolean twoTermination = lexeme.getInflectionClass() == InflectionClass.THIRD;

                 lexeme.getInflections().stream()
                .filter(Agreement.class::isInstance)
                .map(i -> (Agreement) i)
                .forEach(agreement -> {
                    Set<GrammaticalGender> genders = agreement.getGenders();
                    List<Set<GrammaticalGender>> genderGroups;

                    // 3rd decl: split into M+F and N
                    if (twoTermination && genders.containsAll(Set.of(
                            GrammaticalGender.MASCULINE,
                            GrammaticalGender.FEMININE,
                            GrammaticalGender.NEUTER))) {

                        genderGroups = List.of(
                                Set.of(GrammaticalGender.MASCULINE, GrammaticalGender.FEMININE),
                                Set.of(GrammaticalGender.NEUTER)
                        );
                    } else if (!twoTermination ) {
                        // 1st/2nd decl: expand all genders individually
                        genderGroups = genders.stream()
                                .map(Set::of) // singleton set per gender
                                .toList();
                    } else {
                        // all other cases: keep group as-is
                        genderGroups = List.of(new TreeSet<>(genders));
                    }

                    for (Set<GrammaticalGender> genderSet : genderGroups) {
                        table
                                .computeIfAbsent(genderSet, g -> new EnumMap<>(GrammaticalNumber.class))
                                .computeIfAbsent(agreement.getNumber(), n -> new EnumMap<>(GrammaticalCase.class))
                                .put(agreement.getGrammaticalCase(), agreement.getForm());
                    }
                });
        }
        AgreementTableDTO dto = new AgreementTableDTO();
        dto.setInflectionTable(table);

        return dto;
    }
}
