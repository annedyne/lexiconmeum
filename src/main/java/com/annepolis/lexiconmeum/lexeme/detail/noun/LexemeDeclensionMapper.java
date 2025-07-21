package com.annepolis.lexiconmeum.lexeme.detail.noun;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeInflectionMapper;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
class LexemeDeclensionMapper implements LexemeInflectionMapper<Declension> {

    @Override
    public DeclensionTableDTO toInflectionTableDTO(Lexeme<Declension> lexeme) {

        Map<GrammaticalNumber, Map<GrammaticalCase, String>> table = new EnumMap<>(GrammaticalNumber.class);
        if(lexeme != null) {
            for (Declension declension : lexeme.getInflections()) {

                table.computeIfAbsent(declension.getNumber(), numberKey -> new EnumMap<>(GrammaticalCase.class))
                        .put(declension.getGrammaticalCase(), declension.getForm());
            }
        }
        DeclensionTableDTO dto = new DeclensionTableDTO();
        dto.setTable(table);

        return dto;
    }

}
