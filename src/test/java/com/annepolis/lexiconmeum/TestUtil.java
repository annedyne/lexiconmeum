package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.textsearch.Inflection;

public class TestUtil {

    public static Lexeme getNewTestLexeme(){
        Lexeme lexeme = new Lexeme();

        lexeme.getInflections().add(new Inflection("singular","nominative" , "amīcus"));
        lexeme.getInflections().add(new Inflection("singular", "accusative", "amīcum"));
        lexeme.getInflections().add(new Inflection("singular", "vocative", "amīce"));
        lexeme.getInflections().add(new Inflection("singular", "genitive", "amīcī"));
        lexeme.getInflections().add(new Inflection("singular", "dative", "amīcō"));
        lexeme.getInflections().add(new Inflection("singular","ablative", "amīcō" ));

        lexeme.getInflections().add(new Inflection("plural", "nominative", "amīcī"));
        lexeme.getInflections().add(new Inflection("plural", "accusative", "amīcōs"));
        lexeme.getInflections().add(new Inflection("plural", "vocative", "amīcī"));
        lexeme.getInflections().add(new Inflection("plural", "genitive", "amīcōrum"));
        lexeme.getInflections().add(new Inflection("plural", "dative", "amīcīs"));
        lexeme.getInflections().add(new Inflection("plural", "ablative", "amīcīs"));

        return lexeme;

    }
}
