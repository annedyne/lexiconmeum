package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class PartOfSpeechParserRegistryConfig {

    @Bean
    public Map<POSParserKey, PartOfSpeechParser> partOfSpeechParserRegistry(
            POSVerbParser verbParser,
            POSNounParser nounParser,
            POSAdjectiveParser adjectiveParser,
            POSParticipleParser participleParser,
            POSNonInflectedFormParser nonInflectedFormParser
    ) {
        Map<POSParserKey, PartOfSpeechParser> map = new EnumMap<>(POSParserKey.class);
        map.put(POSParserKey.ADVERB, nonInflectedFormParser);
        map.put(POSParserKey.CONJUNCTION, nonInflectedFormParser);
        map.put(POSParserKey.VERB, verbParser);
        map.put(POSParserKey.NOUN, nounParser);

        map.put(POSParserKey.ADJECTIVE_POSITIVE, adjectiveParser);
        map.put(POSParserKey.ADJECTIVE_COMPARATIVE, adjectiveParser);
        map.put(POSParserKey.ADJECTIVE_SUPERLATIVE, adjectiveParser);

        map.put(POSParserKey.DETERMINER, adjectiveParser);
        map.put(POSParserKey.PREPOSITION, nonInflectedFormParser);
        map.put(POSParserKey.POSTPOSITION, nonInflectedFormParser);
        map.put(POSParserKey.PRONOUN, adjectiveParser);
        map.put(POSParserKey.PARTICIPLE, participleParser);
        return map;
    }
}
