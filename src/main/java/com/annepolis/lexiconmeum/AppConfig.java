package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.ingest.wiktionary.LoadProperties;
import com.annepolis.lexiconmeum.webapi.WebApiProperties;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.TextSearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({
        LoadProperties.class,
        TextSearchProperties.class,
        WebApiProperties.class
})
public class AppConfig {

    // No-op status handler lets integration tests inspect 4xx/5xx ResponseEntity without catching exceptions.
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultStatusHandler(status -> true, (request, response) -> {})
                .build();
    }
}
