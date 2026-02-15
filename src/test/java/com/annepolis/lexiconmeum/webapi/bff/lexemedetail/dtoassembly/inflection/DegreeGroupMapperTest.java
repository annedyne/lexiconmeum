package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeFixtureFactory;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDegreeAgreementSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;

import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender.MASCULINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DegreeGroupMapperTest {

    private final AgreementTableMapper agreementTableMapper = new AgreementTableMapper();
    private final DegreeGroupMapper degreeGroupMapper = new DegreeGroupMapper(agreementTableMapper);
    private final ObjectMapper objectMapper = new ObjectMapper();

    static final String POSITIVE = "positive";

    @Test
    void thirdDecl_twoTermination_groupsMandF() throws Exception {
        //build a two-termination adjective that has the same form for all three genders
        Lexeme lexeme = LexemeFixtureFactory.generateSyntheticAdjectiveLexeme(
                AdjectiveTerminationType.TWO_TERMINATION,
                List.of(
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(MASCULINE, GrammaticalGender.FEMININE, GrammaticalGender.NEUTER),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, "formX"
                        )
                )
        );

        InflectionTableDTO dto = degreeGroupMapper.toInflectionTableDTO(lexeme);
        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(dto));
        ArrayNode agreements = (ArrayNode) root.get(POSITIVE);
        assertThat(agreements).hasSize(2); // two columns - one for [M,F] and one for [N]
    }

    // ... existing code ...

    @Test
    void toInflectionTableDTO_generatesAllThreeDegrees() {
        AgreementTableMapper agreementTableMapper = mock(AgreementTableMapper.class);
        when(agreementTableMapper.toInflectionTableDTO(anyList(), anyBoolean()))
                .thenReturn(mock(AgreementTableDTO.class));

        DegreeGroupMapper degreeGroupMapper = new DegreeGroupMapper(agreementTableMapper);

        Lexeme lexeme = LexemeFixtureFactory.generateSyntheticAdjectiveLexeme(
                AdjectiveTerminationType.TWO_TERMINATION,
                List.of(
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(MASCULINE, GrammaticalGender.FEMININE, GrammaticalGender.NEUTER),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, "formX"
                        )
                )
        );

        AdjectiveDetails details = (AdjectiveDetails) lexeme.getPartOfSpeechDetails();
        AdjectiveDegreeAgreementSet comparativeSet = details.getDegreeInflections().get(GrammaticalDegree.COMPARATIVE);
        AdjectiveDegreeAgreementSet superlativeSet = details.getDegreeInflections().get(GrammaticalDegree.SUPERLATIVE);

        boolean expectedPositiveTwoTermination = details.getTerminationType() == AdjectiveTerminationType.TWO_TERMINATION;
        boolean expectedComparativeTwoTermination = comparativeSet.getInflectionClasses().contains(InflectionClass.THIRD);
        boolean expectedSuperlativeTwoTermination = superlativeSet.getInflectionClasses().contains(InflectionClass.THIRD);

        degreeGroupMapper.toInflectionTableDTO(lexeme);

        ArgumentCaptor<List<Inflection>> inflectionsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Boolean> twoTerminationCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(agreementTableMapper, times(3))
                .toInflectionTableDTO(inflectionsCaptor.capture(), twoTerminationCaptor.capture());
        verifyNoMoreInteractions(agreementTableMapper);

        List<List<Inflection>> capturedInflections = inflectionsCaptor.getAllValues();
        List<Boolean> capturedTwoTermination = twoTerminationCaptor.getAllValues();

        assertThat(capturedInflections).hasSize(3);
        assertThat(capturedTwoTermination).hasSize(3);

        // Positive degree call must be present
        assertThat(capturedInflections).contains(lexeme.getInflections());
        int positiveIdx = capturedInflections.indexOf(lexeme.getInflections());
        assertThat(capturedTwoTermination.get(positiveIdx)).isEqualTo(expectedPositiveTwoTermination);

        // Comparative + Superlative booleans must both be present (order-independent)
        assertThat(capturedTwoTermination)
                .contains(expectedComparativeTwoTermination, expectedSuperlativeTwoTermination);
    }

    @Test
    void toInflectionTableDTO_whenComparativeAgreementSetMissing_returnsPartialResponse() {
        AgreementTableMapper agreementTableMapper = mock(AgreementTableMapper.class);
        when(agreementTableMapper.toInflectionTableDTO(anyList(), anyBoolean()))
                .thenReturn(mock(AgreementTableDTO.class));

        DegreeGroupMapper degreeGroupMapper = new DegreeGroupMapper(agreementTableMapper);

        Lexeme lexeme = LexemeFixtureFactory.generateSyntheticAdjectiveLexemeWithNoComparative(
                AdjectiveTerminationType.TWO_TERMINATION,
                List.of(
                        LexemeFixtureFactory.generateSyntheticAgreement(
                                Set.of(MASCULINE, GrammaticalGender.FEMININE, GrammaticalGender.NEUTER),
                                GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE, "formX"
                        )
                )
        );

        AdjectiveDetails details = (AdjectiveDetails) lexeme.getPartOfSpeechDetails();
        degreeGroupMapper.toInflectionTableDTO(lexeme);

        ArgumentCaptor<List<Inflection>> inflectionsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Boolean> twoTerminationCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(agreementTableMapper, times(3))
                .toInflectionTableDTO(inflectionsCaptor.capture(), twoTerminationCaptor.capture());
        verifyNoMoreInteractions(agreementTableMapper);

        List<List<Inflection>> capturedInflections = inflectionsCaptor.getAllValues();
        List<Boolean> capturedTwoTermination = twoTerminationCaptor.getAllValues();

        // Positive still generated from lexeme inflections
        assertThat(capturedInflections).contains(lexeme.getInflections());
        int positiveIdx = capturedInflections.indexOf(lexeme.getInflections());
        assertThat(capturedTwoTermination.get(positiveIdx))
                .isEqualTo(details.getTerminationType() == AdjectiveTerminationType.TWO_TERMINATION);

        // Missing comparative set should degrade to an empty inflection list and "not two-termination" flag
        assertThat(capturedInflections).contains(List.of());
        int emptyIdx = capturedInflections.indexOf(List.of());
        assertThat(capturedTwoTermination.get(emptyIdx)).isFalse();
    }
}
