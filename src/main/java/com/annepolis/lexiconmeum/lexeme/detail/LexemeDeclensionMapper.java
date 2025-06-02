package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
class LexemeDeclensionMapper {

    public DeclensionTableDTO toDeclensionTableDTO(Lexeme lexeme) {

        Map<GrammaticalNumber, Map<GrammaticalCase, String>> table = new EnumMap<>(GrammaticalNumber.class);
        if(lexeme != null) {
            for (Inflection inflection : lexeme.getInflections()) {
                if(inflection instanceof Declension declension){
                    GrammaticalNumber number = declension.getNumber();
                    GrammaticalCase grammaticalCase = declension.getGrammaticalCase();
                    String form = declension.getForm();

                    table.computeIfAbsent(number, n -> new EnumMap<>(GrammaticalCase.class))
                            .put(grammaticalCase, form);
                }
            }
        }
        DeclensionTableDTO dto = new DeclensionTableDTO();
        dto.setTable(table);

        return dto;
    }
}
