package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class PartOfSpeechParserRegistryConfig {

    @Bean
    public Map<PartOfSpeech, PartOfSpeechParser> partOfSpeechParserRegistry(
            POSVerbParser verbParser,
            POSNounParser nounParser,
            POSAdjectiveParser adjectiveParser
    ) {
        Map<PartOfSpeech, PartOfSpeechParser> map = new EnumMap<>(PartOfSpeech.class);
        map.put(PartOfSpeech.VERB, verbParser);
        map.put(PartOfSpeech.NOUN, nounParser);
        map.put(PartOfSpeech.ADJECTIVE, adjectiveParser);
        return map;
    }
}
