package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase.*;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalMood.INDICATIVE;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalMood.INFINITIVE;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber.PLURAL;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber.SINGULAR;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPerson.*;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition.VERB;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense.PRESENT;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalVoice.ACTIVE;

public class TestUtil {

    public static Lexeme getNewTestNounLexeme(){

        LexemeBuilder lexBuilder = new LexemeBuilder("amīcus", GrammaticalPosition.NOUN);


        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, NOMINATIVE,  "amīcus"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, ACCUSATIVE, "amīcum"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, VOCATIVE, "amīce"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, GENITIVE, "amīcī"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, ABLATIVE, "amīce"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, DATIVE, "amīce"));
        lexBuilder.addInflection(getNewTestDeclension( SINGULAR, VOCATIVE, "amīce"));

        lexBuilder.addInflection(getNewTestDeclension( PLURAL, NOMINATIVE, "amīcī"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, ACCUSATIVE, "amīcōs"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, VOCATIVE,  "amīcī"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, GENITIVE,  "amīcōrum"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, ABLATIVE, "amīcīs"));
        lexBuilder.addInflection(getNewTestDeclension( PLURAL, DATIVE, "amīcīs"));

        return lexBuilder.setGender(GrammaticalGender.MASCULINE).build();

    }

    private static Declension getNewTestDeclension(GrammaticalNumber number, GrammaticalCase gramCase, String form) {
        return new Declension.Builder( form).setGrammaticalCase(gramCase).setNumber(number).build();
    }
    public static Lexeme getNewTestVerbLexeme(){
        LexemeBuilder builder = new LexemeBuilder("amo", VERB);

        Conjugation.Builder conjugationBuilder = new Conjugation.Builder("amāre");
        conjugationBuilder.setVoice(ACTIVE).setMood(INFINITIVE).setTense(PRESENT);
        builder.addInflection(conjugationBuilder.build());

        conjugationBuilder = new Conjugation.Builder("amō");
        conjugationBuilder.setVoice(ACTIVE).setMood(INDICATIVE).setPerson(FIRST).setTense(PRESENT).setNumber(SINGULAR);
        builder.addInflection(conjugationBuilder.build());

        conjugationBuilder = new Conjugation.Builder("amās");
        conjugationBuilder.setVoice(ACTIVE).setMood(INDICATIVE).setPerson(SECOND).setTense(PRESENT).setNumber(SINGULAR);
        builder.addInflection(conjugationBuilder.build());

        conjugationBuilder = new Conjugation.Builder("amat");
        conjugationBuilder.setVoice(ACTIVE).setMood(INDICATIVE).setPerson(THIRD).setTense(PRESENT).setNumber(SINGULAR);
        builder.addInflection(conjugationBuilder.build());

        conjugationBuilder = new Conjugation.Builder("amāmus");
        conjugationBuilder.setVoice(ACTIVE).setMood(INDICATIVE).setPerson(FIRST).setTense(PRESENT).setNumber(PLURAL);
        builder.addInflection(conjugationBuilder.build());

        conjugationBuilder = new Conjugation.Builder("amātis");
        conjugationBuilder.setVoice(ACTIVE).setMood(INDICATIVE).setPerson(SECOND).setTense(PRESENT).setNumber(PLURAL);
        builder.addInflection(conjugationBuilder.build());

        conjugationBuilder = new Conjugation.Builder("amant");
        conjugationBuilder.setVoice(ACTIVE).setMood(INDICATIVE).setPerson(THIRD).setTense(PRESENT).setNumber(PLURAL);
        builder.addInflection(conjugationBuilder.build());



        return builder.build();

    }

    public static List<Lexeme> getMixedPositionTestLexemes(){
        List<Lexeme> lexemes = new ArrayList<>();

        lexemes.add(getNewTestVerbLexeme());
        lexemes.add(getNewTestNounLexeme());
        return lexemes;
    }





}
