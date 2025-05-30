package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.lexeme.detail.*;
import com.annepolis.lexiconmeum.shared.Lexeme;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static Lexeme getNewTestNounLexeme(){
        Lexeme lexeme = new Lexeme("amīcus", "noun");

        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.SINGULAR, GrammaticalCase.NOMINATIVE , "amīcus"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.SINGULAR, GrammaticalCase.ACCUSATIVE, "amīcum"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.SINGULAR, GrammaticalCase.VOCATIVE, "amīce"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.SINGULAR, GrammaticalCase.GENITIVE, "amīcī"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.SINGULAR, GrammaticalCase.ABLATIVE, "amīcō"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.SINGULAR, GrammaticalCase.DATIVE, "amīcō" ));

        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.PLURAL, GrammaticalCase.NOMINATIVE, "amīcī"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.PLURAL, GrammaticalCase.ACCUSATIVE, "amīcōs"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.PLURAL, GrammaticalCase.VOCATIVE,  "amīcī"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.PLURAL, GrammaticalCase.GENITIVE,  "amīcōrum"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.PLURAL, GrammaticalCase.ABLATIVE, "amīcīs"));
        lexeme.getInflections().add(new Declension(GrammaticalGender.MASCULINE, GrammaticalNumber.PLURAL, GrammaticalCase.DATIVE, "amīcīs"));

        return lexeme;

    }

    public static Lexeme getNewTestVerbLexeme(){
        Lexeme lexeme = new Lexeme("amare", "verb");
        lexeme.getInflections().add(new Conjugation("amabilis"));
        lexeme.getInflections().add(new Conjugation("amandare"));
        lexeme.getInflections().add(new Conjugation("amandatio"));
        lexeme.getInflections().add(new Conjugation("amor"));

        return lexeme;

    }

    public static List<Lexeme> getMixedPositionTestLexemes(){
        List<Lexeme> lexemes = new ArrayList<>();

        lexemes.add(getNewTestVerbLexeme());
        lexemes.add(getNewTestNounLexeme());
        return lexemes;
    }





}
