package com.annepolis.lexiconmeum.web;

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

import static com.annepolis.lexiconmeum.web.ApiRoutes.PREFIX;
import static com.annepolis.lexiconmeum.web.ApiRoutes.SUFFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
}
