package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

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
        Map<POSParserKey, PartOfSpeechParser> posParsers = new EnumMap<>(POSParserKey.class);
        posParsers.put(POSParserKey.ADVERB, nonInflectedFormParser);
        posParsers.put(POSParserKey.CONJUNCTION, nonInflectedFormParser);
        posParsers.put(POSParserKey.VERB, verbParser);
        posParsers.put(POSParserKey.NOUN, nounParser);

        posParsers.put(POSParserKey.ADJECTIVE_POSITIVE, adjectiveParser);
        posParsers.put(POSParserKey.ADJECTIVE_COMPARATIVE, adjectiveParser);
        posParsers.put(POSParserKey.ADJECTIVE_SUPERLATIVE, adjectiveParser);

        posParsers.put(POSParserKey.DETERMINER, adjectiveParser);
        posParsers.put(POSParserKey.PREPOSITION, nonInflectedFormParser);
        posParsers.put(POSParserKey.POSTPOSITION, nonInflectedFormParser);
        posParsers.put(POSParserKey.PRONOUN, adjectiveParser);
        posParsers.put(POSParserKey.PARTICIPLE, participleParser);

        Set<POSParserKey> missing = EnumSet.allOf(POSParserKey.class);
        missing.removeAll(posParsers.keySet());

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "No PartOfSpeechParser configured for POSParserKey: " + missing);
        }
        return posParsers;
    }
}
