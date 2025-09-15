package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.inflection.Declension;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class DeclensionTableMapper implements InflectionTableMapper {

    @Override
    public DeclensionTableDTO toInflectionTableDTO(Lexeme lexeme) {

        Map<GrammaticalNumber, Map<GrammaticalCase, String>> table = new EnumMap<>(GrammaticalNumber.class);
        if(lexeme != null) {
            lexeme.getInflections().stream()
                    .filter(Declension.class::isInstance)
                    .map(i -> (Declension) i)
                    .forEach(declension ->
                        table.computeIfAbsent(declension.getNumber(), n -> new EnumMap<>(GrammaticalCase.class))
                                .put(declension.getGrammaticalCase(), declension.getForm()));
        }
        DeclensionTableDTO dto = new DeclensionTableDTO();
        dto.setInflectionTable(table);

        return dto;
    }

}
