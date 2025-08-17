package com.annepolis.lexiconmeum.lexeme.detail;
import com.annepolis.lexiconmeum.lexeme.detail.adjective.LexemeAgreementMapper;
import com.annepolis.lexiconmeum.lexeme.detail.noun.LexemeDeclensionMapper;
import com.annepolis.lexiconmeum.lexeme.detail.noun.NounPrincipalPartsSectionContributor;
import com.annepolis.lexiconmeum.lexeme.detail.verb.LexemeConjugationMapper;
import com.annepolis.lexiconmeum.lexeme.detail.verb.VerbPrincipalPartsSectionContributor;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class LexemeDetailPipelineConfig {

    @Bean
    public LexemeDetailPipeline lexemeDetailAssembler(
            IdentitySectionContributor identityContributor,
            DefinitionsSectionContributor definitionsContributor,
            InflectionClassSectionContributor inflectionClassContributor,
            InflectionTableSectionContributor inflectionTableContributor,
            NounPrincipalPartsSectionContributor nounPrincipalPartsContributor,
            VerbPrincipalPartsSectionContributor verbPrincipalPartsContributor

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
        return new LexemeDetailPipeline(pipelines);
    }

    @Bean
    public InflectionTableSectionContributor inflectionTableContributor(
            LexemeConjugationMapper lexemeConjugationMapper,
            LexemeDeclensionMapper lexemeDeclensionMapper,
            LexemeAgreementMapper lexemeAgreementMapper

    ){
        Map<GrammaticalPosition, LexemeInflectionMapper> mappers = new EnumMap<>(GrammaticalPosition.class);
        mappers.put(GrammaticalPosition.VERB, lexemeConjugationMapper);
        mappers.put(GrammaticalPosition.NOUN, lexemeDeclensionMapper);
        mappers.put(GrammaticalPosition.ADJECTIVE, lexemeAgreementMapper);
        return new InflectionTableSectionContributor(mappers);
    }

    @Bean
    public InflectionClassSectionContributor inflectionClassContributor(){
        return new InflectionClassSectionContributor(
                EnumSet.of(GrammaticalPosition.VERB,
                        GrammaticalPosition.NOUN,
                        GrammaticalPosition.ADJECTIVE)
        );
    }
}

