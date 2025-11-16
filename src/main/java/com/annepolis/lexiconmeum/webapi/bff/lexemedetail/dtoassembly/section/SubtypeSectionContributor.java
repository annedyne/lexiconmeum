package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.SyntacticSubtypeDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("subtypeSectionContributor")
public class SubtypeSectionContributor implements LexemeDetailSectionContributor  {

    Set<PartOfSpeech> partsOfSpeech;

    public SubtypeSectionContributor (Set<PartOfSpeech> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }
    @Override
    public boolean supports(Lexeme lexeme) {
        return false;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        if( lexeme.getPartOfSpeechDetails() instanceof SyntacticSubtypeDetails details){
            dto.setSyntacticSubtype( details.getSyntacticSubtype());
        }
        if( lexeme.getPartOfSpeechDetails() instanceof VerbDetails details){
            dto.setMorphologicalSubtype( details.getMorphologicalSubtype());
        }
    }
}
