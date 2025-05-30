package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
class LexemeDeclensionMapper {

    public DeclensionTableDTO toDeclensionTableDTO(Lexeme lexeme) {

        Map<String, Map<String, String>> table = new HashMap<>();
        if(lexeme != null) {
            for (Inflection inflection : lexeme.getInflections()) {
                if(inflection instanceof Declension declension){
                    String number = declension.getNumber().name();
                    String grammaticalCase = declension.getGrammaticalCase().name();
                    String form = declension.getForm();

                    table.computeIfAbsent(number, n -> new HashMap<>())
                            .put(grammaticalCase, form);
                }
            }
        }
        DeclensionTableDTO dto = new DeclensionTableDTO();
        dto.setTable(table);

        return dto;
    }
}
