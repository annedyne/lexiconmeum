package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.lexeme.detail.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.Lexeme;

public class TestUtil {

    public static Lexeme getNewTestLexeme(){
        Lexeme lexeme = new Lexeme();

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



}
