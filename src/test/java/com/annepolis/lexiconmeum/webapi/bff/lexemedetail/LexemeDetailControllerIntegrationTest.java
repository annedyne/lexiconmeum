package com.annepolis.lexiconmeum.webapi.bff.lexemedetail;

import com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager;
import com.annepolis.lexiconmeum.shared.exception.ErrorResponse;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.webapi.ApiRoutes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    private RestClient restClient;

    String getFullBaseUrl(){
        return baseUrl + ":" + port + path;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ErrorResponse readErrorResponse(ResponseEntity<String> response) {
        return objectMapper.readValue(response.getBody(), ErrorResponse.class);
    }

    @Test
    void testLexemeEndpoint() {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("amo", PartOfSpeech.VERB, "1");
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEMES)
                .queryParam("lexemeId", lexemeId.toString())
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDetailEndpoint() {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("poculum", PartOfSpeech.NOUN, "1");
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl()) // full base, e.g., https://lexicon.annedyne.net/api/v1
                .path(ApiRoutes.LEXEME_DETAIL)   // path must start with a slash
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testDetailEndpointWitAdjectiveId() {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("celer", PartOfSpeech.ADJECTIVE, "1");
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl()) // full base, e.g., https://lexicon.annedyne.net/api/v1
                .path(ApiRoutes.LEXEME_DETAIL)   // path must start with a slash
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDetailWithIdAndTypeEndpoint() throws IOException {
        UUID lexemeId = JsonTestDataManager.INSTANCE.getParsedVerbLexeme("amo", "testDataVerb.jsonl").getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .queryParam("type", "VERB")
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        logger.info("Pretty printed DTO:\n{}", prettyJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testTypeMismatchReturnsConflict() {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("poculum", PartOfSpeech.NOUN, "1");
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .queryParam("type", "VERB")
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        ErrorResponse error = readErrorResponse(response);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, error.status());
        assertEquals("Conflict", error.error());
        assertEquals("Type mismatch for lexeme 7842cc92-0202-3bd0-92e0-6df4d9dcfa30: Expected VERB but got NOUN", error.message());
        assertEquals(path + ApiRoutes.LEXEME_DETAIL.replace("{id}", lexemeId.toString()), error.path());
        assertEquals(lexemeId.toString(), error.lexemeId());    }

    @Test
    void testMissingLexemeReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .buildAndExpand(nonExistentId)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
		String body = response.getBody();

		assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(body.contains("Lexeme not found"), "Body should contain the error message");
        assertTrue(body.contains(nonExistentId.toString()), "Body should contain the element ID");

        ErrorResponse error = readErrorResponse(response);
        assertEquals(404, error.status());
        assertEquals("Not Found", error.error());
        assertEquals("Lexeme not found", error.message());
        assertEquals(path + ApiRoutes.LEXEME_DETAIL.replace("{id}", nonExistentId.toString()), error.path());
        assertEquals(nonExistentId.toString(), error.lexemeId());
    }

    @Test
    void testInvalidTypeParameterReturnsBadRequest() {
        LexemeBuilder lexemeBuilder = new LexemeBuilder("amo", PartOfSpeech.VERB, "1");
        UUID lexemeId = lexemeBuilder.build().getId();

        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .queryParam("type", "NOT_A_REAL_TYPE")
                .buildAndExpand(lexemeId)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        ErrorResponse error = readErrorResponse(response);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, error.status());
        assertEquals("Bad Request", error.error());
        assertTrue(error.message().contains("Invalid value 'NOT_A_REAL_TYPE'"));
        assertTrue(error.message().contains("for 'type'"));
        assertEquals(path + ApiRoutes.LEXEME_DETAIL.replace("{id}", lexemeId.toString()), error.path());
        assertNull(error.lexemeId());
    }

    @Test
    void testMissingLexemeIdParameterReturnsBadRequest() {
        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEMES)
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        ErrorResponse error = readErrorResponse(response);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, error.status());
        assertEquals("Bad Request", error.error());
        assertEquals("Missing required parameter 'lexemeId'.", error.message());
        assertEquals(path + ApiRoutes.LEXEMES, error.path());
        assertNull(error.lexemeId());
    }


    @Test
    void testInvalidLexemeIdFormatReturnsBadRequest() {
        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEMES)
                .queryParam("lexemeId", "not-a-uuid")
                .toUriString();

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);
        ErrorResponse error = readErrorResponse(response);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, error.status());
        assertEquals("Bad Request", error.error());
        assertNotNull(error.message());
        assertEquals(path + ApiRoutes.LEXEMES, error.path());
        assertNull(error.lexemeId());
    }
}
