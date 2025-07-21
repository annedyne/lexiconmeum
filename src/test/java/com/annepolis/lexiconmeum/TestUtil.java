package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.*;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.Sense;

import java.util.ArrayList;
import java.util.List;

import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase.*;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalMood.*;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber.PLURAL;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber.SINGULAR;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPerson.*;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition.VERB;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense.PERFECT;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense.PRESENT;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalVoice.ACTIVE;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalVoice.PASSIVE;

public class TestUtil {

    public static Lexeme<Declension> getNewTestNounLexeme(){

        return new LexemeBuilder<Declension>("amīcus", GrammaticalPosition.NOUN)


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
    public static Lexeme<Conjugation> getNewTestVerbLexeme(){

        LexemeBuilder<Conjugation> builder = new LexemeBuilder<>("amo", VERB);

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

    private static void buildConjugation(LexemeBuilder<Conjugation> lexemeBuilder, String form, GrammaticalVoice voice, GrammaticalMood mood,
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

    public static List<Lexeme<?>> getMixedPositionTestLexemes(){
        List<Lexeme<?>> lexemes = new ArrayList<>();

        lexemes.add(getNewTestVerbLexeme());
        lexemes.add(getNewTestNounLexeme());
        return lexemes;
    }
}
