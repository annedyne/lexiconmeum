package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.DeterminerDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

@Component("determinerSubclassSectionContributor")
public class DeterminerSubclassSectionContributor implements LexemeDetailSectionContributor {

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getPartOfSpeech() == PartOfSpeech.DETERMINER;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        if (lexeme.getPartOfSpeechDetails() instanceof DeterminerDetails details) {
            dto.setSyntacticSubtype(details.syntacticSubtype());
        }
    }
}
