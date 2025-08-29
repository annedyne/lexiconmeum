package com.annepolis.lexiconmeum.webapi.bff.textsearch.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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

import static com.annepolis.lexiconmeum.webapi.ApiRoutes.PREFIX;
import static com.annepolis.lexiconmeum.webapi.ApiRoutes.SUFFIX;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.text-search.default-limit=8",
                "app.text-search.result-limit-max=15"
        }
)
@ActiveProfiles("test")
class TextSearchControllerIntegrationTest {

    static final Logger logger = LogManager.getLogger(TextSearchControllerIntegrationTest.class);

    @Value("${test.base-url}")
    private String baseUrl;

    @Value("${api.base-path}")
    private String path;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    String getFullBaseUrl(){
        return baseUrl + ":" + port + path;
    }

    @Test
    void testPrefixSearchEndpoint() {
        String url = getFullBaseUrl() + PREFIX + "?prefix=ama";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        logger.info(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSuffixSearchEndpoint() {
        String url = getFullBaseUrl() + SUFFIX + "?suffix=re";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        logger.info(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testTextSuggestionResultClientLimit() throws JsonProcessingException {
        String url = getFullBaseUrl() + PREFIX + "?prefix=ama" + "&limit=12";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String result = response.getBody();
        assertNotNull(result);

        JsonNode root = objectMapper.readTree(result);

        // Make sure it's an array and check its size
        assertTrue(root.isArray(), "Response should be a JSON array");
        assertTrue(root.size() <= 12, "Should return no more than 12 suggestions");
    }

    @Test
    void testTextSuggestionResultClientLimitMax() throws JsonProcessingException {
        String url = getFullBaseUrl() + PREFIX + "?prefix=ama" + "&limit=100";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String result = response.getBody();
        assertNotNull(result);

        JsonNode root = objectMapper.readTree(result);

        // Make sure it's an array and check its size
        assertTrue(root.isArray(), "Response should be a JSON array");
        assertTrue(root.size() <= 15, "Should return no more than 15 suggestions");
    }

    @Test
    void testTextSuggestionResultLimitDefault() throws JsonProcessingException {
        String url = getFullBaseUrl() + PREFIX + "?prefix=am";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String result = response.getBody();
        assertNotNull(result);

        JsonNode root = objectMapper.readTree(result);

        // Make sure it's an array and check its size
        assertTrue(root.isArray(), "Response should be a JSON array");
        logger.info(root.size());
        assertTrue(root.size() <= 8, "Should return no more than 8 suggestions");
    }
}
