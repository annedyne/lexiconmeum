package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.Sense;

import java.util.List;
import java.util.Map;

public abstract class AbstractLexemeDetailMapper {

    public final LexemeDetailResponse toLexemeDetailDTO(Lexeme lexeme){
        var dto = new LexemeDetailResponse();

        dto.setLemma(lexeme.getLemma());
        setInflectionClass(dto, lexeme);

        populateDefinitions(dto, lexeme.getSenses());
        populatePrincipalParts(dto, lexeme.getInflectionIndex());


        dto.setInflectionTableDTO( buildTable(lexeme) );
        return dto;
    }

    protected abstract void setInflectionClass(LexemeDetailResponse dto, Lexeme lexeme);

    private void populateDefinitions(LexemeDetailResponse dto, List<Sense> senses){
        senses.stream().flatMap(s -> s.getGloss().stream())
                .toList().forEach(dto::addDefinition);
    }
    protected abstract InflectionTableDTO buildTable(Lexeme lexeme);

    /** Hook—default does nothing; override only where needed **/
    protected void populatePrincipalParts(LexemeDetailResponse dto,
                                          Map<String, Inflection> inflectionIndex) {
        // no-op
    }

}
