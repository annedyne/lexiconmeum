package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDegreeAgreementSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DegreeGroupMapper implements InflectionTableMapper {

    AgreementTableMapper agreementTableMapper;

    public DegreeGroupMapper(AgreementTableMapper agreementTableMapper) {
        this.agreementTableMapper = agreementTableMapper;
    }

    @Override
    public DegreeGroupDTO toInflectionTableDTO(Lexeme lexeme) {
        if (!(lexeme.getPartOfSpeechDetails() instanceof AdjectiveDetails details)) {
            String actual = (lexeme.getPartOfSpeechDetails() == null)
                    ? "null"
                    : lexeme.getPartOfSpeechDetails().getClass().getSimpleName();

            throw new IllegalStateException(
                    "Invariant violation: DegreeGroupMapper requires AdjectiveDetails but got " + actual
                            + " (lexemeId=" + lexeme.getId()
                            + ", lemma=" + lexeme.getLemma()
                            + ", partOfSpeech=" + lexeme.getPartOfSpeech() + ")"
            );
        }

        boolean positiveTwoOrOneTermination = details.getTerminationType() == AdjectiveTerminationType.TWO_TERMINATION
                || details.getTerminationType() == AdjectiveTerminationType.ONE_TERMINATION;
        AgreementTableDTO positive = generateAgreementTableDTO(lexeme.getInflections(), positiveTwoOrOneTermination);

        AdjectiveDegreeAgreementSet comparativeAgreementSet =
                (details.getDegreeInflections() == null)
                        ? null
                        : details.getDegreeInflections().get(GrammaticalDegree.COMPARATIVE);

        boolean comparativeTwoTermination = isThirdDeclensionIndicator(comparativeAgreementSet);
        AgreementTableDTO comparative = generateAgreementTableDTO(
                (comparativeAgreementSet == null) ? List.of() : comparativeAgreementSet.getInflections(),
                comparativeTwoTermination
        );

        AdjectiveDegreeAgreementSet superlativeAgreementSet =
                (details.getDegreeInflections() == null)
                        ? null
                        : details.getDegreeInflections().get(GrammaticalDegree.SUPERLATIVE);

        boolean superlativeTwoTermination = isThirdDeclensionIndicator(superlativeAgreementSet);
        AgreementTableDTO superlative = generateAgreementTableDTO(
                (superlativeAgreementSet == null) ? List.of() : superlativeAgreementSet.getInflections(),
                superlativeTwoTermination
        );

        return new DegreeGroupDTO(positive, comparative, superlative);
    }

    private static boolean isThirdDeclensionIndicator(AdjectiveDegreeAgreementSet agreementSet) {
        if (agreementSet == null) {
            return false;
        }
        Set<InflectionClass> classes = agreementSet.getInflectionClasses();
        return classes != null && classes.contains(InflectionClass.THIRD);
    }

    AgreementTableDTO generateAgreementTableDTO(List<Inflection> inflections, boolean twoOrOneTermination) {
        return agreementTableMapper.toInflectionTableDTO(inflections, twoOrOneTermination);
    }
}
