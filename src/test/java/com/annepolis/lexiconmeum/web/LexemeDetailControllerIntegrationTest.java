package com.annepolis.lexiconmeum.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static com.annepolis.lexiconmeum.web.ApiRoutes.DECLENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LexemeDetailControllerIntegrationTest {

    static final Logger LOGGER = LogManager.getLogger(LexemeDetailControllerIntegrationTest.class);

    @Value("${test.base-url}")
    private String baseUrl;

    @Value("${api.base-path}")
    private String path;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    String getFullBaseUrl(){
        return baseUrl + ":" + port + path;
    }


    @Test
    void testPrefixSearchEndpoint() throws JsonProcessingException {
        String url = getFullBaseUrl() + DECLENSION + "?lexemeId=poculumnoun";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        LOGGER.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
