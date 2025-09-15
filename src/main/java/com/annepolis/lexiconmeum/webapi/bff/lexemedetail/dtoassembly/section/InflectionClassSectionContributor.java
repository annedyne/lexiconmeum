package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.grammar.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("inflectionClassSectionContributor")
class InflectionClassSectionContributor implements LexemeDetailSectionContributor {
    private final Set<PartOfSpeech> partsOfSpeech;

    public InflectionClassSectionContributor(Set<PartOfSpeech> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    @Override
    public boolean supports(Lexeme lexeme) {
        return partsOfSpeech.contains(lexeme.getPartOfSpeech());
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        String display = lexeme.getInflectionClasses().stream()
                .map(InflectionClass::getDisplayTag)
                .collect(Collectors.joining(" & "));
        if (!display.isBlank()) {
            String suffix = lexeme.getPartOfSpeech().getInflectionType();
            dto.setInflectionClass(display + " " + suffix);
        }
    }

}
