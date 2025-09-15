package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.ingest.wiktionary.LoadProperties;
import com.annepolis.lexiconmeum.webapi.WebApiProperties;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.TextSearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        LoadProperties.class,
        TextSearchProperties.class,
        WebApiProperties.class
})
public class AppConfig {
}
