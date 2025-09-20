package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.AgreementTableMapper;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.ConjugationTableMapper;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.DeclensionTableMapper;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.InflectionTableMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
 class LexemeDetailResponseAssemblerConfig {

    @Bean
    LexemeDetailResponseAssembler lexemeDetailAssembler(
           @Qualifier("identitySectionContributor") LexemeDetailSectionContributor identityContributor,
           @Qualifier("definitionsSectionContributor") LexemeDetailSectionContributor definitionsContributor,
           @Qualifier("inflectionClassSectionContributor") LexemeDetailSectionContributor inflectionClassContributor,
           @Qualifier("inflectionTableSectionContributor") LexemeDetailSectionContributor inflectionTableContributor,
           @Qualifier("nounPrincipalPartsSectionContributor") LexemeDetailSectionContributor nounPrincipalPartsContributor,
           @Qualifier("verbPrincipalPartsSectionContributor") LexemeDetailSectionContributor verbPrincipalPartsContributor,
           @Qualifier("nounGenderSectionContributor") LexemeDetailSectionContributor nounGenderSectionContributor,
           @Qualifier("prepositionCaseSectionContributor") LexemeDetailSectionContributor prepositionCaseSectionContributor

           // Add/remove contributors as needed
    ) {
        Map<PartOfSpeech, List<LexemeDetailSectionContributor>> pipelines = new EnumMap<>(PartOfSpeech.class);

        var common = List.of(identityContributor, definitionsContributor);

        for (PartOfSpeech partOfSpeech : PartOfSpeech.values()) {
            pipelines.put(partOfSpeech, new ArrayList<>(common));
        }

        pipelines.get(PartOfSpeech.VERB).addAll(List.of(
                inflectionTableContributor,
                verbPrincipalPartsContributor,
                inflectionClassContributor
        ));

        pipelines.get(PartOfSpeech.NOUN).addAll(List.of(
                inflectionTableContributor,
                nounPrincipalPartsContributor,
                nounGenderSectionContributor,
                inflectionClassContributor
        ));

        pipelines.get(PartOfSpeech.ADJECTIVE).addAll(List.of(
                inflectionTableContributor,
                inflectionClassContributor
        ));

        pipelines.get(PartOfSpeech.PREPOSITION).addAll(List.of(
                prepositionCaseSectionContributor
        ));
        pipelines.get(PartOfSpeech.POSTPOSITION).addAll(List.of(
                prepositionCaseSectionContributor
        ));
        return new LexemeDetailResponseAssembler(pipelines);
    }

    @Bean
    Map<PartOfSpeech, InflectionTableMapper> inflectionMappers(
            ConjugationTableMapper conjugationTableMapper,
            DeclensionTableMapper lexemeDeclensionMapper,
            AgreementTableMapper lexemeAgreementMapper
    ) {
        Map<PartOfSpeech, InflectionTableMapper> mappers = new EnumMap<>(PartOfSpeech.class);
        mappers.put(PartOfSpeech.VERB, conjugationTableMapper);
        mappers.put(PartOfSpeech.NOUN, lexemeDeclensionMapper);
        mappers.put(PartOfSpeech.ADJECTIVE, lexemeAgreementMapper);
        return mappers;
    }

    @Bean
    Set<PartOfSpeech> inflectionClassPartOfSpeechs() {
        return EnumSet.of(
                PartOfSpeech.VERB,
                PartOfSpeech.NOUN,
                PartOfSpeech.ADJECTIVE
        );
    }

}

