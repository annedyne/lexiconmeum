package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.LexemeBuilder;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

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
    void testLexemeEndpoint() throws JsonProcessingException {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("amo", GrammaticalPosition.VERB);
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEMES)
                .queryParam("lexemeId", lexemeId.toString())
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDetailEndpoint() throws JsonProcessingException {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("poculum", GrammaticalPosition.NOUN);
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl()) // full base, e.g., https://lexicon.annedyne.net/api/v1
                .path(ApiRoutes.LEXEME_DETAIL)   // path must start with a slash
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDetailWithIdAndTypeEndpoint() throws JsonProcessingException {
        UUID lexemeId = TestUtil.getNewTestVerbLexeme().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .queryParam("type", "VERB")
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testTypeMismatchReturnsConflict() {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("poculum", GrammaticalPosition.NOUN);
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .queryParam("type", "VERB")
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        logger.info("Type mismatch error message: {}", response.getBody());
    }

    @Test
    void testMissingLexemeReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .buildAndExpand(nonExistentId)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Lexeme not found", response.getBody());
    }
}
