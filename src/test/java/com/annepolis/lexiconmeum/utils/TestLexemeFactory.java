package com.annepolis.lexiconmeum.utils;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;

import java.util.List;
import java.util.Set;

public final class TestLexemeFactory {

    private TestLexemeFactory() {}

    public static Agreement agreement(Set<GrammaticalGender> genders,
                                      GrammaticalNumber number,
                                      GrammaticalCase grammaticalCase,
                                      String form) {
        Agreement.Builder builder = new Agreement.Builder(form);
        builder.setGenders(genders).setNumber(number).setGrammaticalCase(grammaticalCase);
        return builder.build();
    }

    public static Lexeme adjective(AdjectiveTerminationType terminationType, List<Agreement> agreements) {

        AdjectiveDetails details = new AdjectiveDetails( terminationType);

        LexemeBuilder lexemeBuilder = new LexemeBuilder("test-adj", GrammaticalPosition.ADJECTIVE, "1" );
        lexemeBuilder.setPartOfSpeechDetails(details);

        for (Agreement a : agreements) {
            lexemeBuilder.addInflection(a);
        }
        return lexemeBuilder.build();
    }
}

