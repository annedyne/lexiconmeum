package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.utils.TestLexemeFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AgreementTableMapperTest {

    private final AgreementTableMapper agreementTableMapper = new AgreementTableMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    static final String NEUTER_FORM = "acre";
    static final String MASCULINE_FORM = "acris";
    static final String FEMININE_FORM = "acer";

    //json keys
    static final String AGREEMENTS = "agreements";
    static final String INFLECTIONS = "inflections";
    static final String GENDERS = "genders";
    @Test
    void thirdDecl_threeTermination_expandsToSingletons() throws Exception {
        // Build a 3-termination Adjective Lexeme with three Nominative Singular Agreement entries, one for each gender
        Lexeme lexeme = TestLexemeFactory.adjective(
                 AdjectiveTerminationType.THREE_TERMINATION,
                List.of(
                        TestLexemeFactory.agreement(
                                Set.of(MASCULINE),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, MASCULINE_FORM),
                        TestLexemeFactory.agreement(
                                Set.of(GrammaticalGender.FEMININE),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, FEMININE_FORM),
                        TestLexemeFactory.agreement(
                                Set.of(GrammaticalGender.NEUTER),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, NEUTER_FORM)
                )
        );

        InflectionTableDTO dto = agreementTableMapper.toInflectionTableDTO(lexeme);
        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(dto));
        ArrayNode agreements = (ArrayNode) root.get(AGREEMENTS);

        assertThat(agreements).hasSize(3); // [M], [F], [N]

        Map<Set<String>, String> gendersToForm =
                JsonAsserts.gendersToFormAt(agreements, GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE);
        assertThat(gendersToForm)
                .containsEntry(Set.of(MASCULINE.name()), MASCULINE_FORM)
                .containsEntry(Set.of(FEMININE.name()), FEMININE_FORM)
                .containsEntry(Set.of(NEUTER.name()), NEUTER_FORM);
    }

    @Test
    void thirdDecl_twoTermination_groupsMandF() throws Exception {
        //build a two-termination adjective that has the same form for all three genders
        Lexeme lexeme = TestLexemeFactory.adjective(
                AdjectiveTerminationType.TWO_TERMINATION,
                List.of(
                        TestLexemeFactory.agreement(
                                Set.of(MASCULINE, GrammaticalGender.FEMININE, GrammaticalGender.NEUTER),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, "formX")
                )
        );

        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(agreementTableMapper.toInflectionTableDTO(lexeme)));
        ArrayNode agreements = (ArrayNode) root.get(AGREEMENTS);
        assertThat(agreements).hasSize(2); // two columns - one for [M,F] and one for [N]

        Map<Set<String>, String> gendersToForm =
                JsonAsserts.gendersToFormAt(agreements, GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE);
        assertThat(gendersToForm)
                .containsEntry(Set.of(MASCULINE.name(), FEMININE.name()), "formX")
                .containsEntry(Set.of(NEUTER.name()), "formX");
    }

    @Test
    void firstSecondDecl_notTwoTermination_expandsToSingletons() throws Exception {
        // !TWO_TERMINATION => expand {M,F,N} into three singleton entries
        //build a three-termination adjective with a single form for all three genders
        Lexeme lexeme = TestLexemeFactory.adjective(
                AdjectiveTerminationType.THREE_TERMINATION, // or a dedicated 1st/2nd type in your model
                List.of(
                        TestLexemeFactory.agreement(
                                Set.of(MASCULINE, GrammaticalGender.FEMININE, GrammaticalGender.NEUTER),
                                GrammaticalNumber.PLURAL, GrammaticalCase.ACCUSATIVE, "formX")
                )
        );

        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(agreementTableMapper.toInflectionTableDTO(lexeme)));
        ArrayNode agreements = (ArrayNode) root.get(AGREEMENTS);
        assertThat(agreements).hasSize(3);
    }


    static class JsonAsserts {
        static Map<Set<String>, String> gendersToFormAt(ArrayNode agreements,
                                                        GrammaticalNumber number,
                                                        GrammaticalCase grammaticalCase) {
            Map<Set<String>, String> out = new LinkedHashMap<>();
            for (JsonNode node : agreements) {
                Set<String> genders = new LinkedHashSet<>();
                for (JsonNode g : node.get(GENDERS)) genders.add(g.asText());
                String form = node.get(INFLECTIONS).get(number.name()).get(grammaticalCase.name()).asText();
                out.put(genders, form);
            }
            return out;
        }
    }

}
