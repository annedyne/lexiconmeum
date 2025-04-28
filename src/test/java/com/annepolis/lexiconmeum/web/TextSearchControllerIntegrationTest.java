package com.annepolis.lexiconmeum.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TextSearchControllerIntegrationTest {

    static final Logger LOGGER = LogManager.getLogger(TextSearchControllerIntegrationTest.class);
    private static final String API = "/api/search/";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testPrefixSearchEndpoint() {
        String url = "http://localhost:" + port + API + "/api/search/prefix?prefix=ama";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        LOGGER.info(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSuffixSearchEndpoint() {
        String url = "http://localhost:" + port + API + "suffix?suffix=re";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        LOGGER.info(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
