package com.annepolis.lexiconmeum.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "test")
@Profile("test")
public class TestProperties {

    private Resource dataFile;
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Resource getDataFile() {
        return dataFile;
    }

    public void setDataFile(Resource dataFile) {
        this.dataFile = dataFile;
    }
}
