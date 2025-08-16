package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.lexeme.detail.adjective.Agreement;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class InflectionKey {

    public static final String KEY_DELIMITER = "|";

    public static String of(Inflection inflection){
        if (inflection instanceof Conjugation c) {
            return buildConjugationKey(c);
        } else if (inflection instanceof Declension d ){
            return buildDeclensionKey(d);
        } else if (inflection instanceof Agreement a ){
            return buildAgreementKey(a);
        } else {
            throw new IllegalArgumentException("Unsupported inflection type: " + inflection.getClass());
        }
    }
    public static String buildConjugationKey(Conjugation conjugation) {
        return joinParts(
                conjugation.getVoice(),
                conjugation.getMood(),
                conjugation.getTense(),
                conjugation.getPerson(),
                conjugation.getNumber()
                );
    }

    public static String buildDeclensionKey(Declension declension) {
        return joinDeclensionParts(declension.getGrammaticalCase(), declension.getNumber());
    }

    public static String buildAgreementKey(Agreement agreement) {
        return joinAgreementParts(agreement.getGrammaticalCase(), agreement.getNumber(), agreement.getGenders(), agreement.getDegree());
    }

    public static String joinParts(
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalPerson person,
            GrammaticalNumber number
    ){
        return buildKeyPart(voice, true)
        + buildKeyPart(mood)
        + buildKeyPart(tense)
        + buildKeyPart(person)
        + buildKeyPart(number);
    }

    private static String buildKeyPart(Enum<?>  part){
        return buildKeyPart(part, false);
    }

    private static String buildKeyPart(Enum<?>  part, boolean firstOne){
        String delimiter = firstOne ? "" : KEY_DELIMITER;
       return part != null ? delimiter + part.name() : "";
    }

    public String buildFirstPrincipalPartKey() {
        return buildConjugationPrincipalPartKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PRESENT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }

    public String buildSecondPrincipalPartKey() {
        return buildConjugationPrincipalPartKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INFINITIVE,
                GrammaticalTense.PRESENT,
                null,
                null
        );
    }

    public String buildThirdPrincipalPartKey() {
        return buildConjugationPrincipalPartKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }

    private static String buildConjugationPrincipalPartKey(
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalPerson person,
            GrammaticalNumber number
    ) {
        return joinParts( voice, mood, tense, person, number );
    }

    public String buildFirstDeclensionPrincipalPartKey(){
        return joinDeclensionParts(GrammaticalCase.NOMINATIVE, GrammaticalNumber.SINGULAR);
    }
    public String buildSecondDeclensionPrincipalPartKey(){
        return joinDeclensionParts(GrammaticalCase.GENITIVE, GrammaticalNumber.SINGULAR);
    }

    public static String joinDeclensionParts(
            GrammaticalCase grammaticalCase, GrammaticalNumber number ) {

        return buildKeyPart(grammaticalCase, true)
        + buildKeyPart(number);
    }

    public static String joinAgreementParts(
            GrammaticalCase grammaticalCase, GrammaticalNumber number, Set<GrammaticalGender> genders, GrammaticalDegree degree ) {
        String genderPart = genders.stream()
                .sorted()
                .map(g -> buildKeyPart(g))
                .collect(Collectors.joining());
        return buildKeyPart(grammaticalCase, true)
                + buildKeyPart(number)
                + genderPart
                + buildKeyPart(degree);
    }
}
