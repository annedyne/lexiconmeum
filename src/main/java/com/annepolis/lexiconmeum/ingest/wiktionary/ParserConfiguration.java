package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfiguration {

    @Bean
    public ParseMode parseMode(LoadProperties props) {
        return props.getParseMode();
    }
}
