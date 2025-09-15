package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "app.load")
public class LoadProperties {
    private Resource dataFile;
    private ParseMode parseMode;


    public ParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ParseMode mode) {
        this.parseMode = mode;
    }

    public Resource getDataFile() {
        return dataFile;
    }

    public void setDataFile(Resource dataFile) {
        this.dataFile = dataFile;
    }

}
