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

    public static Lexeme getNewTestNounLexeme(){

        LexemeBuilder lexBuilder = new LexemeBuilder("amīcus", GrammaticalPosition.NOUN);


        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, NOMINATIVE,  "amīcus"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, ACCUSATIVE, "amīcum"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, VOCATIVE, "amīce"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, GENITIVE, "amīcī"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, ABLATIVE, "amīcō"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, DATIVE, "amīcō"));

        lexBuilder.addInflection(getNewTestDeclension( PLURAL, NOMINATIVE, "amīcī"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, ACCUSATIVE, "amīcōs"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, VOCATIVE,  "amīcī"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, GENITIVE,  "amīcōrum"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, ABLATIVE, "amīcīs"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, DATIVE, "amīcīs"));

        return lexBuilder.setGender(GrammaticalGender.MASCULINE).build();

    }



    private static Declension getNewTestDeclension(GrammaticalNumber number, GrammaticalCase gramCase, String form) {
        return new Declension.Builder(form).setGrammaticalCase(gramCase).setNumber(number).build();
    }
    public static Lexeme getNewTestVerbLexeme(){
        LexemeBuilder builder = new LexemeBuilder("amo", VERB);

        Conjugation.Builder conjugationBuilder = new Conjugation.Builder("amāre");
        conjugationBuilder.setVoice(ACTIVE).setMood(INFINITIVE).setTense(PRESENT);
        builder.addInflection(conjugationBuilder.build());


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



        builder.addInflection(conjugationBuilder.build());
        builder.addSense(getNewTestSense());
        return builder.build();

    }

    private static void buildConjugation(LexemeBuilder builder, String form, GrammaticalVoice voice, GrammaticalMood mood,
                                  GrammaticalPerson person, GrammaticalTense tense, GrammaticalNumber number){

            Conjugation.Builder conjugationBuilder = new Conjugation.Builder(form);
            conjugationBuilder.setVoice(voice).setMood(mood).setPerson(person).setTense(tense).setNumber(number);
            builder.addInflection(conjugationBuilder.build());
    }

    private static Sense getNewTestSense(){
        Sense.Builder builder = new Sense.Builder();
        builder.addGloss("to love");
        return builder.build();
    }

    public static List<Lexeme> getMixedPositionTestLexemes(){
        List<Lexeme> lexemes = new ArrayList<>();

        lexemes.add(getNewTestVerbLexeme());
        lexemes.add(getNewTestNounLexeme());
        return lexemes;
    }
}
