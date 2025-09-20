package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PrepositionDetails;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

@Component
public class PrepositionCaseSectionContributor implements LexemeDetailSectionContributor {

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getPartOfSpeech() == PartOfSpeech.PREPOSITION ||
                lexeme.getPartOfSpeech() == PartOfSpeech.POSTPOSITION;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        if( lexeme.getPartOfSpeechDetails() instanceof PrepositionDetails){
            dto.setGovernedCase(((PrepositionDetails) lexeme.getPartOfSpeechDetails()).governedCase());
        }
    }
}
