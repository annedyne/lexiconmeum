package com.annepolis.lexiconmeum.shared.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

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
            } catch (JsonProcessingException e) {
                logger.warn("Could not serialize DTO", e);
            }
        }
    }
}
