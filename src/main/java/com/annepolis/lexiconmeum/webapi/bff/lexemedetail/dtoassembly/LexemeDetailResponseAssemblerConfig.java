package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
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
           @Qualifier("verbPrincipalPartsSectionContributor") LexemeDetailSectionContributor verbPrincipalPartsContributor

            // Add/remove contributors as needed
    ) {
        Map<GrammaticalPosition, List<LexemeDetailSectionContributor>> pipelines = new EnumMap<>(GrammaticalPosition.class);

        var common = List.of(identityContributor, definitionsContributor);

        for (GrammaticalPosition position : GrammaticalPosition.values()) {
            pipelines.put(position, new ArrayList<>(common));
        }

        pipelines.get(GrammaticalPosition.VERB).addAll(List.of(
                inflectionTableContributor,
                verbPrincipalPartsContributor,
                inflectionClassContributor
        ));

        pipelines.get(GrammaticalPosition.NOUN).addAll(List.of(
                inflectionTableContributor,
                nounPrincipalPartsContributor,
                inflectionClassContributor
        ));

        pipelines.get(GrammaticalPosition.ADJECTIVE).addAll(List.of(
                inflectionTableContributor,
                inflectionClassContributor
        ));
        return new LexemeDetailResponseAssembler(pipelines);
    }

    @Bean
    Map<GrammaticalPosition, InflectionTableMapper> inflectionMappers(
            ConjugationTableMapper conjugationTableMapper,
            DeclensionTableMapper lexemeDeclensionMapper,
            AgreementTableMapper lexemeAgreementMapper
    ) {
        Map<GrammaticalPosition, InflectionTableMapper> mappers = new EnumMap<>(GrammaticalPosition.class);
        mappers.put(GrammaticalPosition.VERB, conjugationTableMapper);
        mappers.put(GrammaticalPosition.NOUN, lexemeDeclensionMapper);
        mappers.put(GrammaticalPosition.ADJECTIVE, lexemeAgreementMapper);
        return mappers;
    }

    @Bean
    Set<GrammaticalPosition> inflectionClassPositions() {
        return EnumSet.of(
                GrammaticalPosition.VERB,
                GrammaticalPosition.NOUN,
                GrammaticalPosition.ADJECTIVE
        );
    }

}

