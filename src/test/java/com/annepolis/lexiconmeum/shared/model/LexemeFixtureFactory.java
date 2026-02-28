package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDegreeAgreementSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;

import java.util.*;
import java.util.stream.Stream;

public final class LexemeFixtureFactory {

    private LexemeFixtureFactory() {}

    public static Agreement generateSyntheticAgreement(Set<GrammaticalGender> genders,
                                                       GrammaticalNumber number,
                                                       GrammaticalCase grammaticalCase,
                                                       String form) {
        Agreement.Builder builder = new Agreement.Builder(form);
        builder.setGenders(genders).setNumber(number).setGrammaticalCase(grammaticalCase);
        return builder.build();
    }

    public static Lexeme generateSyntheticAdjectiveLexeme(AdjectiveTerminationType terminationType, List<Agreement> agreements ) {
        return generateSyntheticAdjectiveLexeme(terminationType, agreements, false);
    }

    public static Lexeme generateSyntheticAdjectiveLexemeWithNoComparative(AdjectiveTerminationType terminationType, List<Agreement> agreements ) {
        return generateSyntheticAdjectiveLexeme(terminationType, agreements, true);
    }


    private static Lexeme generateSyntheticAdjectiveLexeme(AdjectiveTerminationType terminationType, List<Agreement> agreements, boolean skipComparative) {

        AdjectiveDetails.Builder builder = new AdjectiveDetails.Builder();
        builder.setAdjectiveTerminationType(terminationType);

        if (!skipComparative) {
            // Create synthetic comparative degree agreement set
            AdjectiveDegreeAgreementSet comparativeSet = new AdjectiveDegreeAgreementSet(
                    "test-adj-comparative",
                    GrammaticalDegree.COMPARATIVE,
                    terminationType == AdjectiveTerminationType.TWO_TERMINATION
                            ? Set.of(InflectionClass.THIRD)
                            : Set.of(InflectionClass.FIRST, InflectionClass.SECOND)
            );
            comparativeSet.setInflectionIndex(new HashMap<>());
            builder.addDegreeInflectionSet(comparativeSet);
        }

        // Create synthetic superlative degree agreement set
        AdjectiveDegreeAgreementSet superlativeSet = new AdjectiveDegreeAgreementSet(
                "test-adj-superlative",
                GrammaticalDegree.SUPERLATIVE,
                terminationType == AdjectiveTerminationType.TWO_TERMINATION
                        ? Set.of(InflectionClass.THIRD)
                        : Set.of(InflectionClass.FIRST, InflectionClass.SECOND)
        );
        superlativeSet.setInflectionIndex(new HashMap<>());
        builder.addDegreeInflectionSet(superlativeSet);

        AdjectiveDetails details = builder.build();

        LexemeBuilder lexemeBuilder = new LexemeBuilder("test-adj", PartOfSpeech.ADJECTIVE, "1");
        lexemeBuilder.setPartOfSpeechDetails(details);

        for (Agreement a : agreements) {
            lexemeBuilder.addInflection(a);
        }
        return lexemeBuilder.build();
    }

    public static Stream<String> expectedPulcherForms() {
        List<String> forms = new ArrayList<>();

        for (GrammaticalDegree degree : GrammaticalDegree.values()) {
            for (GrammaticalGender gender : GrammaticalGender.values()) {
                for (GrammaticalNumber number : GrammaticalNumber.values()) {
                    for (GrammaticalCase grammaticalCase : GrammaticalCase.values()) {
                        String form = generateForm(gender, number, grammaticalCase);
                        forms.add(form);
                    }
                }
            }
        }

        return forms.stream().filter(Objects::nonNull);
    }



    public static String generateForm(
            GrammaticalGender gender, GrammaticalNumber number,
            GrammaticalCase grammaticalCase) {
        if (gender == GrammaticalGender.MASCULINE) {
            if (number == GrammaticalNumber.SINGULAR) {
                return switch (grammaticalCase) {
                    case NOMINATIVE -> "pulcher";
                    case GENITIVE -> "pulchrī";
                    case DATIVE -> "pulchrō";
                    case ABLATIVE -> "pulchrō";
                    case ACCUSATIVE -> "pulchrum";
                    case VOCATIVE -> "pulcher";
                    default -> "pulcher";
                };
            } else {
                return switch (grammaticalCase) {
                    case NOMINATIVE -> "pulchrī";
                    case GENITIVE -> "pulchrōrum";
                    case DATIVE -> "pulchrīs";
                    case ABLATIVE -> "pulchrīs";
                    case ACCUSATIVE -> "pulchrōs";
                    case VOCATIVE -> "pulchrī";
                    default -> "pulchrī";
                };
            }
        } else if (gender == GrammaticalGender.NEUTER) {
            if (number == GrammaticalNumber.SINGULAR) {
                return switch (grammaticalCase) {
                    case NOMINATIVE -> "pulchrum";
                    case GENITIVE -> "pulchrī";
                    case DATIVE -> "pulchrō";
                    case ABLATIVE -> "pulchrō";
                    case ACCUSATIVE -> "pulchrum";
                    case VOCATIVE -> "pulcher";
                    default -> "pulcher";
                };
            } else {
                return switch (grammaticalCase) {
                    case NOMINATIVE -> "pulchra";
                    case GENITIVE -> "pulchrōrum";
                    case DATIVE -> "pulchrīs";
                    case ABLATIVE -> "pulchrīs";
                    case ACCUSATIVE -> "pulchra";
                    case VOCATIVE -> "pulchra";
                    default -> "pulchra";
                };
            }
        } else {
            if (number == GrammaticalNumber.SINGULAR) {
                return switch (grammaticalCase) {
                    case NOMINATIVE -> "pulchra";
                    case GENITIVE -> "pulchrae";
                    case DATIVE -> "pulchrae";
                    case ABLATIVE -> "pulchrā";
                    case ACCUSATIVE -> "pulchram";
                    default -> "pulchra";
                };
            } else {
                return switch (grammaticalCase) {
                    case NOMINATIVE -> "pulchrae";
                    case GENITIVE -> "pulchrārum";
                    case DATIVE -> "pulchrīs";
                    case ABLATIVE -> "pulchrīs";
                    case ACCUSATIVE -> "pulchrās";
                    default -> "pulchrae";
                };
            }
        }
    }
}

