package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.lexeme.detail.adjective.Agreement;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase.*;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalMood.*;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber.PLURAL;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber.SINGULAR;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPerson.*;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition.VERB;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense.PERFECT;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense.PRESENT;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice.ACTIVE;
import static com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalVoice.PASSIVE;

public class TestUtil {

    public static Lexeme getNewTestNounLexeme(){

        return new LexemeBuilder("amīcus", GrammaticalPosition.NOUN, "1")


        .addInflection(getNewTestDeclension( SINGULAR, NOMINATIVE,  "amīcus"))
        .addInflection(getNewTestDeclension( SINGULAR, ACCUSATIVE, "amīcum"))
        .addInflection(getNewTestDeclension( SINGULAR, VOCATIVE, "amīce"))
        .addInflection(getNewTestDeclension( SINGULAR, GENITIVE, "amīcī"))
        .addInflection(getNewTestDeclension( SINGULAR, ABLATIVE, "amīcō"))
        .addInflection(getNewTestDeclension( SINGULAR, DATIVE, "amīcō"))

        .addInflection(getNewTestDeclension( PLURAL, NOMINATIVE, "amīcī"))
        .addInflection(getNewTestDeclension( PLURAL, ACCUSATIVE, "amīcōs"))
        .addInflection(getNewTestDeclension( PLURAL, VOCATIVE,  "amīcī"))
        .addInflection(getNewTestDeclension( PLURAL, GENITIVE,  "amīcōrum"))
        .addInflection(getNewTestDeclension( PLURAL, ABLATIVE, "amīcīs"))
        .addInflection(getNewTestDeclension( PLURAL, DATIVE, "amīcīs"))

         .setGender(GrammaticalGender.MASCULINE).build();

    }



    private static Declension getNewTestDeclension(GrammaticalNumber number, GrammaticalCase gramCase, String form) {
        return new Declension.Builder(form).setGrammaticalCase(gramCase).setNumber(number).build();
    }
    public static Lexeme getNewTestVerbLexeme(){

        LexemeBuilder builder = new LexemeBuilder("amo", VERB, "1");

        buildConjugation(builder, "amō", ACTIVE, INDICATIVE, FIRST, PRESENT, SINGULAR);
        buildConjugation(builder, "amās", ACTIVE, INDICATIVE, SECOND, PRESENT, SINGULAR);
        buildConjugation(builder, "amat", ACTIVE, INDICATIVE, THIRD, PRESENT, SINGULAR);
        buildConjugation(builder, "amāmus", ACTIVE, INDICATIVE, FIRST, PRESENT, PLURAL);
        buildConjugation(builder, "amātis", ACTIVE, INDICATIVE, SECOND, PRESENT, PLURAL);
        buildConjugation(builder, "amant", ACTIVE, INDICATIVE, THIRD, PRESENT, PLURAL);

        buildConjugation(builder, "amem", ACTIVE, SUBJUNCTIVE, FIRST, PRESENT, SINGULAR);
        buildConjugation(builder, "ames", ACTIVE, SUBJUNCTIVE, SECOND, PRESENT, SINGULAR);
        buildConjugation(builder, "amet", ACTIVE, SUBJUNCTIVE, THIRD, PRESENT, SINGULAR);
        buildConjugation(builder, "amēmus", ACTIVE, SUBJUNCTIVE, FIRST, PRESENT, PLURAL);
        buildConjugation(builder, "amētis", ACTIVE, SUBJUNCTIVE, SECOND, PRESENT, PLURAL);
        buildConjugation(builder, "ament", ACTIVE, SUBJUNCTIVE, THIRD, PRESENT, PLURAL);

        buildConjugation(builder, "amor", PASSIVE, INDICATIVE, FIRST, PRESENT, SINGULAR);
        buildConjugation(builder, "amāris", PASSIVE, INDICATIVE, SECOND, PRESENT, SINGULAR);
        buildConjugation(builder, "amātur", PASSIVE, INDICATIVE, THIRD, PRESENT, SINGULAR);
        buildConjugation(builder, "amāmur", PASSIVE, INDICATIVE, FIRST, PRESENT, PLURAL);
        buildConjugation(builder, "amāmini", PASSIVE, INDICATIVE, SECOND, PRESENT, PLURAL);
        buildConjugation(builder, "amantur", PASSIVE, INDICATIVE, THIRD, PRESENT, PLURAL);

        buildConjugation(builder, "amer", PASSIVE, SUBJUNCTIVE, FIRST, PRESENT, SINGULAR);
        buildConjugation(builder, "amēris", PASSIVE, SUBJUNCTIVE, SECOND, PRESENT, SINGULAR);
        buildConjugation(builder, "amētur", PASSIVE, SUBJUNCTIVE, THIRD, PRESENT, SINGULAR);
        buildConjugation(builder, "amēmur", PASSIVE, SUBJUNCTIVE, FIRST, PRESENT, PLURAL);
        buildConjugation(builder, "amēmini", PASSIVE, SUBJUNCTIVE, SECOND, PRESENT, PLURAL);
        buildConjugation(builder, "amentur", PASSIVE, SUBJUNCTIVE, THIRD, PRESENT, PLURAL);

        buildConjugation(builder, "amāvī", ACTIVE, INDICATIVE, FIRST, PERFECT, SINGULAR);
        buildConjugation(builder, "amāvīsti", ACTIVE, INDICATIVE, SECOND, PERFECT, SINGULAR);
        buildConjugation(builder, "amāvit", ACTIVE, INDICATIVE, THIRD, PERFECT, SINGULAR);
        buildConjugation(builder, "amāvīmus", ACTIVE, INDICATIVE, FIRST, PERFECT, PLURAL);
        buildConjugation(builder, "amāvītis", ACTIVE, INDICATIVE, SECOND, PERFECT, PLURAL);
        buildConjugation(builder, "amāverunt", ACTIVE, INDICATIVE, THIRD, PERFECT, PLURAL);

        buildConjugation(builder, "amātus", PASSIVE, INDICATIVE, FIRST, PERFECT, SINGULAR);

        Conjugation conjugation = new Conjugation.Builder("amāre")
                .setVoice(ACTIVE).setMood(INFINITIVE).setTense(PRESENT).build();

        return  builder.addInflection(conjugation)
                .addSense(getNewTestSense())
                .build();

    }

    private static void buildConjugation(LexemeBuilder lexemeBuilder, String form, GrammaticalVoice voice, GrammaticalMood mood,
                                         GrammaticalPerson person, GrammaticalTense tense, GrammaticalNumber number){

            Conjugation conjugation = new Conjugation.Builder(form)
            .setVoice(voice).setMood(mood).setPerson(person).setTense(tense).setNumber(number).build();

            lexemeBuilder.addInflection(conjugation);
    }

    private static Sense getNewTestSense(){
        return new Sense.Builder()
        .addGloss("to love")
        .build();
    }

    public static List<Lexeme> getMixedPositionTestLexemes(){
        List<Lexeme> lexemes = new ArrayList<>();

        lexemes.add(getNewTestVerbLexeme());
        lexemes.add(getNewTestNounLexeme());
        return lexemes;
    }

    public static Stream<String> expectedPulcherForms() {
        List<String> forms = new ArrayList<>();

        for (GrammaticalDegree degree : GrammaticalDegree.values()) {
            for (GrammaticalGender gender : GrammaticalGender.values()) {
                for (GrammaticalNumber number : GrammaticalNumber.values()) {
                    for (GrammaticalCase grammaticalCase : GrammaticalCase.values()) {
                        String form = generateForm( gender, number, grammaticalCase);
                        if (form != null) forms.add(form);
                    }
                }
            }
        }

        return forms.stream().filter(Objects::nonNull);
    }

    static String generateForm(
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

    public static Lexeme getNewTestAdjectiveLexeme(){
        LexemeBuilder builder = new LexemeBuilder("pulcher", GrammaticalPosition.ADJECTIVE, "1");
        builder.addInflectionClass(InflectionClass.FIRST);
        builder.addInflectionClass(InflectionClass.SECOND);
        for(Agreement agreement : generateAgreements()){
            builder.addInflection(agreement);
        }
        return builder.build();
    }

    public static List<Agreement> generateAgreements() {
        List<Agreement> agreements = new ArrayList<>();

        for (GrammaticalDegree degree : GrammaticalDegree.values()) {
            for (GrammaticalGender gender : GrammaticalGender.values()) {
                for (GrammaticalNumber number : GrammaticalNumber.values()) {
                    for (GrammaticalCase grammaticalCase : GrammaticalCase.values()) {
                        String form = generateForm( gender, number, grammaticalCase);
                        if (form != null) {
                            agreements.add(buildAgreement(form, gender, number, grammaticalCase));
                        }
                    }
                }
            }
        }
        return agreements;
    }
    private static Agreement buildAgreement(String form, GrammaticalGender gender , GrammaticalNumber number , GrammaticalCase grammaticalCase){
        Agreement.Builder builder = new Agreement.Builder(form);
        return builder.addGender(gender).setNumber(number).setGrammaticalCase(grammaticalCase).build();
    }
}
