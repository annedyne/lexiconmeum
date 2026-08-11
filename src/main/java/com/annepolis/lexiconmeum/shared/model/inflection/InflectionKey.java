package com.annepolis.lexiconmeum.shared.model.inflection;

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
        } else if (inflection instanceof Participle p ){
            return buildParticipleInflectionKey(p);
        } else {
            throw new IllegalArgumentException("Unsupported inflection type: " + inflection.getClass());
        }
    }
    public static String buildConjugationKey(Conjugation conjugation) {
        return buildKey(
                conjugation.getVoice(),
                conjugation.getMood(),
                conjugation.getTense(),
                conjugation.getPerson(),
                conjugation.getNumber(),
                conjugation.getGender()
                );
    }

    public static String buildConjugationKey(
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalPerson person,
            GrammaticalNumber number
    ){
        return buildKey(voice, mood, tense, person, number);
    }

    public String buildFirstPrincipalPartKey() {
        return buildKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PRESENT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }

    public String buildSecondPrincipalPartKey() {
        return buildKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INFINITIVE,
                GrammaticalTense.PRESENT
        );
    }

    public String buildThirdPrincipalPartKey() {
        return buildKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }

    public String buildFourthPrincipalPartKey() {
        return buildKey(
                GrammaticalVoice.PASSIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }


    public static String buildDeclensionKey(Declension declension) {
        return buildKey(declension.getGrammaticalCase(), declension.getNumber());
    }

    public String buildFirstDeclensionPrincipalPartKey(){
        return buildKey(GrammaticalCase.NOMINATIVE, GrammaticalNumber.SINGULAR);
    }

    public String buildSecondDeclensionPrincipalPartKey(){
        return buildKey(GrammaticalCase.GENITIVE, GrammaticalNumber.SINGULAR);
    }


    public static String buildAgreementKey(Agreement agreement) {


        return joinAgreementParts(
                agreement.getGrammaticalCase(),
                agreement.getNumber(),
                agreement.getGenders(),
                agreement.getDegree());
    }

    public static String joinAgreementParts(
            GrammaticalCase grammaticalCase, GrammaticalNumber number, Set<GrammaticalGender> genders, GrammaticalDegree degree ) {
        java.util.List<Enum<?>> parts = new java.util.ArrayList<>();
        parts.add(grammaticalCase);
        parts.add(number);
        genders.stream()
                .sorted()
                .forEach(parts::add);
        parts.add(degree);

        return buildKey(parts.toArray(Enum<?>[]::new));
    }

    /**
     * Not strictly an 'Inflection Key'
     * Builds a key representing a set of participle declensions ('ParticipleSet')
     * for the specified grammatical voice and tense.
     *
     * @param voice the grammatical voice (e.g., ACTIVE, PASSIVE) to be included in the key
     * @param tense the grammatical tense (e.g., PRESENT, IMPERFECT) to be included in the key
     * @return the constructed key as a concatenated string of the voice and tense
     */
    public static String buildParticipleSetKey(GrammaticalVoice voice, GrammaticalTense tense) {
        return buildKey(voice, tense);
    }

    public static String buildVerbalNounParticipleSetKey(GrammaticalTense tense) {
        return buildKey(tense);
    }

    private static String buildKey(Enum<?>... parts) {
        return java.util.Arrays.stream(parts)
                .filter(java.util.Objects::nonNull)
                .map(Enum::name)
                .collect(Collectors.joining(KEY_DELIMITER));
    }


    /**
     * Builds a unique key for a participle's inflectional properties.
     *
     * @param participle the participle whose inflectional properties are used to build the key
     * @return a string representing the unique key for the participle's inflectional properties
     */
    public static String buildParticipleInflectionKey(Participle participle) {
        return buildAgreementKey(new Agreement.Builder(participle.getForm())
                .setGrammaticalCase(participle.getGrammaticalCase())
                .setNumber(participle.getNumber())
                .setGenders(participle.getGenders())
                .setDegree(participle.getDegree())
                .build());
    }
}
