package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDegreeAgreementSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;

public class StagedAdjectiveDegreeData implements LinkableData {
    
    // lemma of positive form
    String parentLemma;
    String parentLemmaWithMacrons;
    AdjectiveDegreeAgreementSet adjectiveDegreeAgreementSet;

    public StagedAdjectiveDegreeData ( String parentLemma, String parentLemmaWithMacrons, AdjectiveDegreeAgreementSet agreementSet){
        this.parentLemma = parentLemma;
        this.parentLemmaWithMacrons = parentLemmaWithMacrons;
        this.adjectiveDegreeAgreementSet = agreementSet;
    }

    @Override
    public String getLemma() {
        return adjectiveDegreeAgreementSet.getDegreeLemma();
    }

    @Override
    public String getLinkingLemma() {
        return parentLemma; 
    }

    @Override
    public String getLinkingLemmaWithMacrons() {
        return parentLemmaWithMacrons;
    }

    @Override
    public Lexeme link(Lexeme lexeme) {
        LexemeBuilder builder = LexemeBuilder.fromLexeme(lexeme);
        AdjectiveDetails.Builder adjectiveDetailsBuilder = getOrCreateAdjectiveDetailsBuilder(lexeme);

        adjectiveDetailsBuilder.addDegreeInflectionSet(adjectiveDegreeAgreementSet);

        return builder.setPartOfSpeechDetails(adjectiveDetailsBuilder.build()).build();
    }

    private AdjectiveDetails.Builder getOrCreateAdjectiveDetailsBuilder(Lexeme adjective) {
        if (adjective.getPartOfSpeechDetails() instanceof AdjectiveDetails adjectiveDetails) {

            AdjectiveDetails.Builder adBuilder = new AdjectiveDetails.Builder();
            adBuilder.setAdjectiveTerminationType(adjectiveDetails.getTerminationType());

            adjectiveDetails.getDegreeInflections().values().forEach(adBuilder::addDegreeInflectionSet);

            return adBuilder;
        }

        // No existing details, create new
        return new AdjectiveDetails.Builder();
    }

    @Override
    public String getDataKey() {
        return adjectiveDegreeAgreementSet.getGrammaticalDegree().name();
    }
}
