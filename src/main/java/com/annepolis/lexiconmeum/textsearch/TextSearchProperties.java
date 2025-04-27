package com.annepolis.lexiconmeum.textsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.text-search")
public class TextSearchProperties {

    private int defaultLimit = 10; // fallback if yaml missing

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }
}
