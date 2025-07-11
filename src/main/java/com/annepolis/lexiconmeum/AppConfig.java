package com.annepolis.lexiconmeum;

import com.annepolis.lexiconmeum.shared.data.load.LoadProperties;
import com.annepolis.lexiconmeum.textsearch.TextSearchProperties;
import com.annepolis.lexiconmeum.web.WebApiProperties;
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
