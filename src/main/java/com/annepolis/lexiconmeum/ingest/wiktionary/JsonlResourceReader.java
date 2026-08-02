package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
class JsonlResourceReader {

    private static final Logger logger = LogManager.getLogger(JsonlResourceReader.class);
    private static final String JSONL_FORMAT_ERROR = "Check that JSONL is correctly formatted and not 'prettified': {}";

    private final ObjectMapper mapper;

    JsonlResourceReader() {
        this(new ObjectMapper());
    }

    JsonlResourceReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    List<JsonNode> readIfPresent(Resource resource) throws IOException {
        if (resource == null || !resource.exists()) {
            return List.of();
        }
        List<JsonNode> nodes = new ArrayList<>();
        read(resource, nodes::add);
        return nodes;
    }

    void read(Resource resource, Consumer<JsonNode> nodeConsumer) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                nodeConsumer.accept(readNode(line, resource));
            }
        }
    }

    private JsonNode readNode(String line, Resource resource) throws JacksonException {
        try {
            return mapper.readTree(line);
        } catch (JacksonException jacksonException) {
            logger.error(JSONL_FORMAT_ERROR, resource.getDescription(), jacksonException);
            throw jacksonException;
        }
    }
}
