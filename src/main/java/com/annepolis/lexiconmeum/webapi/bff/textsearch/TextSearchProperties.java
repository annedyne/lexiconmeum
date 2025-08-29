package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.text-search")
public class TextSearchProperties {

    private int defaultLimit = 8;
    private int resultLimitMax = 20;


    public int getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public int getResultLimitMax() {
        return resultLimitMax;
    }

    public void setResultLimitMax(int resultLimitMax) {
        this.resultLimitMax = resultLimitMax;
    }
}
