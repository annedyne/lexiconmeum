package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeFixtureFactory;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.testsupport.TestSupport;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.*;

import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender.*;
import static org.assertj.core.api.Assertions.assertThat;

class AgreementTableMapperTest {

    private final AgreementTableMapper agreementTableMapper = new AgreementTableMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    static final String NEUTER_FORM = "acre";
    static final String MASCULINE_FORM = "acris";
    static final String FEMININE_FORM = "acer";

    //json keys
    static final String AGREEMENTS = "agreements";
    static final String INFLECTIONS = "inflections";
    static final String GENDERS = "genders";

    /**
     * Wiktionary only provides distinct adjective inflection forms.
     * For the sake of layout consistency, this api provides a full set of forms
     * for each termination.
     * Ex: 3 termination adj gets three sets o forms, one for each 'termination' (M, F, N)
     * Ex: 2 termination adj gets two sets of forms, one for each 'termination' (M&F, N)
     */

    @Test
    void threeTermination_threeForms_expandsToSingletons() throws Exception {
        // Build a 3-termination Adjective Lexeme with three Nominative Singular Agreement entries, one for each gender
        Lexeme lexeme = LexemeFixtureFactory.generateSyntheticAdjectiveLexeme(
                AdjectiveTerminationType.THREE_TERMINATION,
                List.of(
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(MASCULINE),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, MASCULINE_FORM),
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(GrammaticalGender.FEMININE),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, FEMININE_FORM),
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(GrammaticalGender.NEUTER),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, NEUTER_FORM)
                )
        );

        // Picking up only the first DTO in the list which is the main (positive) degree
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
    void threeTermination_oneForm_expandsToSingletons() throws Exception {
        // !TWO_TERMINATION => take a single form (because same form for each gender)
        // and duplicate it for each gender
        // build a three-termination adjective with a single form for all three genders
        Lexeme lexeme = LexemeFixtureFactory.generateSyntheticAdjectiveLexeme(
                AdjectiveTerminationType.THREE_TERMINATION, // or a dedicated 1st/2nd type in your model
                List.of(
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(MASCULINE, GrammaticalGender.FEMININE, GrammaticalGender.NEUTER),
                                GrammaticalNumber.PLURAL, GrammaticalCase.ACCUSATIVE, "formX")
                )
        );

        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(agreementTableMapper.toInflectionTableDTO(lexeme)));
        ArrayNode agreements = (ArrayNode) root.get(AGREEMENTS);
        assertThat(agreements).hasSize(3);
    }

    @Test
    void oneTermination_twoForms_expandsToTwoSets() throws IOException {
        Lexeme lexeme = TestSupport.getInstance().getJsonTestDataManager()
                .getParsedAdjectiveLexeme("caelebs", "testDataAdjective.jsonl");

        boolean isTwoOrOneTermination = true;
        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(agreementTableMapper.toInflectionTableDTO(lexeme.getInflections(), isTwoOrOneTermination)));
        ArrayNode agreements = (ArrayNode) root.get(AGREEMENTS);
        assertThat(agreements).hasSize(2);
    }

    @Test
    void NoTermination_oneForm_expandsToSingletons() throws Exception {
        // !TWO_TERMINATION => take a single form (because same form for each gender)
        // and duplicate it for each gender
        // build a three-termination adjective with a single form for all three genders
        Lexeme lexeme = LexemeFixtureFactory.generateSyntheticAdjectiveLexeme(
                AdjectiveTerminationType.NONE, // or a dedicated 1st/2nd type in your model
                List.of(
                        LexemeFixtureFactory.generateSyntheticAgreement(
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
                for (JsonNode g : node.get(GENDERS)) genders.add(g.asString());
                String form = node.get(INFLECTIONS).get(number.name()).get(grammaticalCase.name()).asString();
                out.put(genders, form);
            }
            return out;
        }
    }
}
