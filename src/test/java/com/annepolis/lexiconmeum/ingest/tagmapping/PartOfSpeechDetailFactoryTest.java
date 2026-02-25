package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.InflectionClass;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDegreeAgreementSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PartOfSpeechDetailFactoryTest {

    @Test
    void detailFactorySetsTwoTerminationToLexemeBuilderWithNullAdjectiveDetails(){

        LexemeBuilder builder = new LexemeBuilder("test", PartOfSpeech.ADJECTIVE, "one");
        PartOfSpeechDetailFactory.TWO_TERMINATION.applyTo(builder);
        if ( builder.getPartOfSpeechDetails() instanceof AdjectiveDetails ad) {
            assertSame(AdjectiveTerminationType.TWO_TERMINATION, ad.getTerminationType());
        }
    }

    @Test
    void setsTwoTerminationOnLexemeBuilderWithExistingAdjectiveDetails(){
        AdjectiveDetails.Builder adBuilder = new AdjectiveDetails.Builder();
        String lemma = "testLemma";
        adBuilder.addDegreeInflectionSet(new AdjectiveDegreeAgreementSet(lemma, GrammaticalDegree.COMPARATIVE, Set.of(InflectionClass.SECOND)));
        LexemeBuilder builder = new LexemeBuilder("test", PartOfSpeech.ADJECTIVE, "one");
        builder.setPartOfSpeechDetails(adBuilder.build());
        PartOfSpeechDetailFactory.TWO_TERMINATION.applyTo(builder);

        if ( builder.getPartOfSpeechDetails() instanceof AdjectiveDetails ad) {
            assertSame(AdjectiveTerminationType.TWO_TERMINATION, ad.getTerminationType());
            assertEquals(lemma, ad.getDegreeInflections().get(GrammaticalDegree.COMPARATIVE).getDegreeLemma());
        }
    }
}
