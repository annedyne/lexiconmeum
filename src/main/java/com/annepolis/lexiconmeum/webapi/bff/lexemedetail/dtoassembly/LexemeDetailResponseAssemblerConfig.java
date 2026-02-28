package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.*;
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
           @Qualifier("prepositionCaseSectionContributor") LexemeDetailSectionContributor prepositionCaseSectionContributor,
           @Qualifier("subtypeSectionContributor") LexemeDetailSectionContributor subtypeSectionContributor

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
                inflectionClassContributor,
                subtypeSectionContributor
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

        pipelines.get(PartOfSpeech.DETERMINER).addAll(List.of(
                inflectionTableContributor,
                inflectionClassContributor,
                subtypeSectionContributor
        ));

        pipelines.get(PartOfSpeech.PRONOUN).addAll(List.of(
                inflectionTableContributor,
                inflectionClassContributor,
                subtypeSectionContributor
        ));

        pipelines.get(PartOfSpeech.PREPOSITION).add(
                prepositionCaseSectionContributor
        );
        pipelines.get(PartOfSpeech.POSTPOSITION).add(
                prepositionCaseSectionContributor
        );
        return new LexemeDetailResponseAssembler(pipelines);
    }

    @Bean
    Map<PartOfSpeech, InflectionTableMapper> inflectionMappers(
            ConjugationGroupMapper conjugationTableMapper,
            DeclensionTableMapper lexemeDeclensionMapper,
            DegreeGroupMapper degreeGroupMapper,
            AgreementTableMapper agreementTableMapper
    ) {
        Map<PartOfSpeech, InflectionTableMapper> mappers = new EnumMap<>(PartOfSpeech.class);
        mappers.put(PartOfSpeech.VERB, conjugationTableMapper);
        mappers.put(PartOfSpeech.NOUN, lexemeDeclensionMapper);
        mappers.put(PartOfSpeech.ADJECTIVE, degreeGroupMapper);
        mappers.put(PartOfSpeech.DETERMINER, agreementTableMapper);
        mappers.put(PartOfSpeech.PRONOUN, agreementTableMapper);
        return mappers;
    }

    @Bean
    Set<PartOfSpeech> inflectionClassPartsOfSpeech() {
        return EnumSet.of(
                PartOfSpeech.VERB,
                PartOfSpeech.NOUN,
                PartOfSpeech.ADJECTIVE,
                PartOfSpeech.DETERMINER,
                PartOfSpeech.PRONOUN
        );
    }

    @Bean
    ConjugationGroupMapper conjugationGroupMapper(
            ConjugationTableMapper conjugationTableMapper,
            ParticipleTableMapper participleTableMapper
    ){
        return new ConjugationGroupMapper(conjugationTableMapper, participleTableMapper);
    }

}

