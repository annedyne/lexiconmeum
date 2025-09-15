package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.NounDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

@Component("nounGenderSectionContributor")
class NounGenderSectionContributor implements LexemeDetailSectionContributor {

    @Override
    public boolean supports(Lexeme lexeme) {
        return lexeme.getPartOfSpeech() == PartOfSpeech.NOUN;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        if (lexeme.getPartOfSpeechDetails() instanceof NounDetails details) {
            dto.setGrammaticalGender(details.grammaticalGender());
        }
    }
}
