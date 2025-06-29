package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.TestUtil;
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

import java.util.UUID;

import static com.annepolis.lexiconmeum.web.ApiRoutes.CONJUGATION;
import static com.annepolis.lexiconmeum.web.ApiRoutes.DECLENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LexemeDetailControllerIntegrationTest {

    static final Logger logger = LogManager.getLogger(LexemeDetailControllerIntegrationTest.class);

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
    void testDeclensionDetailEndpoint() throws JsonProcessingException {
        UUID lexemeId = TestUtil.getNewTestNounLexeme().getId();
        String url = getFullBaseUrl() + DECLENSION + "?lexemeId=" + lexemeId.toString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testConjugationDetailEndpoint() throws JsonProcessingException {
        UUID lexemeId = TestUtil.getNewTestVerbLexeme().getId();
        String url = getFullBaseUrl() + CONJUGATION + "?lexemeId=" + lexemeId.toString();
        //4d6a2666-22a4-3a18-8a56-0c0e6a8ae404
        //ffa9e2b1-6694-3436-8f24-b40ae10caeb3
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
