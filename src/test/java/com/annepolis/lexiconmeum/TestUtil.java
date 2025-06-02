package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.lexeme.detail.Conjugation;
import com.annepolis.lexiconmeum.lexeme.detail.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase.*;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber.PLURAL;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber.SINGULAR;
import static com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition.VERB;

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
        LexemeBuilder builder = new LexemeBuilder("amare", VERB);

        builder.addInflection(new Conjugation("amabilis"));
        builder.addInflection(new Conjugation("amandare"));
        builder.addInflection(new Conjugation("amandatio"));
        builder.addInflection(new Conjugation("amor"));

        return builder.build();

    }

    public static List<Lexeme> getMixedPositionTestLexemes(){
        List<Lexeme> lexemes = new ArrayList<>();

        lexemes.add(getNewTestVerbLexeme());
        lexemes.add(getNewTestNounLexeme());
        return lexemes;
    }





}
