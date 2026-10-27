package com.annepolis.lexiconmeum.shared.exception;

import com.annepolis.lexiconmeum.webapi.ApiRoutes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.annepolis.lexiconmeum.webapi.ApiResponseKeys.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Framework-raised errors (unmapped paths, unsupported methods) must keep their
 * intended 4xx status instead of being swallowed into a 500 by the catch-all handler.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GlobalExceptionHandlerIntegrationTest {

    // A path bots commonly probe for; it is not mapped by this app.
    private static final String UNMAPPED_PATH = "/wp-login.php";

    // Markers that would indicate internals leaking into an error body.
    private static final String EXCEPTION_MARKER = "Exception";
    private static final String STACK_TRACE_MARKER = "trace";

    @Value("${test.base-url}")
    private String baseUrl;

    @Value("${api.base-path}")
    private String path;

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient restClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getFullBaseUrl() {
        return baseUrl + ":" + port + path;
    }

    private JsonNode assertProblemDetail(ResponseEntity<String> response, HttpStatus expected) {
        assertEquals(expected, response.getStatusCode());

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Error response should have a Content-Type");
        assertTrue(MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(contentType),
                "Error response should be application/problem+json but was " + contentType);

        String body = response.getBody();
        assertNotNull(body, "Error response should have a body");
        assertFalse(body.contains(EXCEPTION_MARKER), "Body must not leak exception class names");
        assertFalse(body.contains(STACK_TRACE_MARKER), "Body must not leak a stack trace");

        JsonNode problem = objectMapper.readTree(body);
        assertEquals(expected.value(), problem.path(PROBLEM_STATUS).asInt());
        assertEquals(expected.getReasonPhrase(), problem.path(PROBLEM_TITLE).asString());
        return problem;
    }

    @Test
    void unmappedPathReturnsNotFound() {
        String url = getFullBaseUrl() + UNMAPPED_PATH;

        ResponseEntity<String> response = restClient.get().uri(url).retrieve().toEntity(String.class);

        JsonNode problem = assertProblemDetail(response, HttpStatus.NOT_FOUND);
        assertEquals(path + UNMAPPED_PATH, problem.path(PROBLEM_INSTANCE).asString());
    }

    @Test
    void unsupportedMethodReturnsMethodNotAllowed() {
        String url = UriComponentsBuilder
                .fromUriString(getFullBaseUrl())
                .path(ApiRoutes.LEXEME_DETAIL)
                .buildAndExpand(UUID.randomUUID())
                .toUriString();

        ResponseEntity<String> response = restClient.post().uri(url).retrieve().toEntity(String.class);

        assertProblemDetail(response, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
