package com.annepolis.lexiconmeum.lexeme.detail.verb;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.*;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import org.springframework.stereotype.Component;

@Component
public final class InflectionKey {

    public static String of(Inflection<?> inflection){
        if (inflection instanceof Conjugation c) {
            return buildConjugationKey(c);
        } else if (inflection instanceof Declension d ){
            return buildDeclensionKey(d);
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

    public static String joinParts(
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalPerson person,
            GrammaticalNumber number
    ){
        StringBuilder builder = new StringBuilder();
        builder.append(buildKeyPart(voice, true));
        builder.append(buildKeyPart(mood));
        builder.append(buildKeyPart(tense));
        builder.append(buildKeyPart(person));
        builder.append(buildKeyPart(number));
        return builder.toString();
    }

    private static String buildKeyPart(Enum<?>  part){
        return buildKeyPart(part, false);
    }

    private static String buildKeyPart(Enum<?>  part, boolean firstOne){
        String delimiter = firstOne ? "" : "|";
       return part != null ? delimiter + part.name() : "";
    }

    private static String buildPrinciplePartKey(
            GrammaticalVoice voice,
            GrammaticalMood mood,
            GrammaticalTense tense,
            GrammaticalPerson person,
            GrammaticalNumber number
    ) {
        return joinParts( voice, mood, tense, person, number );
    }

    public String buildFirstPrincipalPartKey() {
        return buildPrinciplePartKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PRESENT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }

    public String buildSecondPrincipalPartKey() {
        return buildPrinciplePartKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INFINITIVE,
                GrammaticalTense.PRESENT,
                null,
                null
        );
    }

    public String buildThirdPrincipalPartKey() {
        return buildPrinciplePartKey(
                GrammaticalVoice.ACTIVE,
                GrammaticalMood.INDICATIVE,
                GrammaticalTense.PERFECT,
                GrammaticalPerson.FIRST,
                GrammaticalNumber.SINGULAR
        );
    }


    public static String buildDeclensionKey(Declension declension) {
        return buildPrinciplePartKey(declension.getGrammaticalCase(), declension.getNumber());
    }

    public String buildFirstDeclensionPrinciplePartKey(){
        return buildPrinciplePartKey(GrammaticalCase.NOMINATIVE, GrammaticalNumber.SINGULAR);
    }
    public String buildSecondDeclensionPrinciplePartKey(){
        return buildPrinciplePartKey(GrammaticalCase.GENITIVE, GrammaticalNumber.SINGULAR);
    }

    private static String buildPrinciplePartKey(
            GrammaticalCase grammaticalCase,GrammaticalNumber number) {
        return joinDeclensionParts( grammaticalCase, number  );
    }

    public static String joinDeclensionParts(
            GrammaticalCase grammaticalCase, GrammaticalNumber number ) {

        StringBuilder builder = new StringBuilder();
        builder.append(buildKeyPart(grammaticalCase, true));
        builder.append(buildKeyPart(number));
        return builder.toString();
    }
}
