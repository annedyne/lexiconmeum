package com.annepolis.lexiconmeum.shared.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonDTOLogger implements JsonLogger{

    private final ObjectMapper mapper;
    public static final Logger logger = LogManager.getLogger(JsonDTOLogger.class);

    public JsonDTOLogger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void logAsJson(Object dto) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("DTO JSON: {}", mapper.writeValueAsString(dto));
            } catch (JacksonException e) {
                logger.warn("Could not serialize DTO", e);
            }
        }
    }
}
