package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class PartOfSpeechParserConfig {

    @Bean
    public Map<PartOfSpeech, PartOfSpeechParser> wiktionaryPosValidators(
            POSVerbParser verbValidator,
            POSNounParser nounValidator,
            POSAdjectiveParser adjectiveValidator
    ) {
        Map<PartOfSpeech, PartOfSpeechParser> map = new EnumMap<>(PartOfSpeech.class);
        map.put(PartOfSpeech.VERB, verbValidator);
        map.put(PartOfSpeech.NOUN, nounValidator);
        map.put(PartOfSpeech.ADJECTIVE, adjectiveValidator);
        return map;
    }
}
