package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.textsearch.Inflection;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LexemeDeclensionMapper {

    public DeclensionTableDTO toDeclensionTableDTO(Lexeme lexeme) {


        Map<String, Map<String, String>> table = new HashMap<>();

        for (Inflection inflection : lexeme.getInflections()) {
            String number = inflection.getNumber();
            String gramCase = inflection.getCase();
            String form = inflection.getForm();

            table.computeIfAbsent(number, n -> new HashMap<>())
                    .put(gramCase, form);
        }

        DeclensionTableDTO dto = new DeclensionTableDTO();
        dto.setTable(table);

        return dto;
    }
}
