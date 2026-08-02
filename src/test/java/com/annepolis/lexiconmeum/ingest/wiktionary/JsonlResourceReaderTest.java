package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.annepolis.lexiconmeum.testsupport.TestSupport.jsonLine;
import static com.annepolis.lexiconmeum.testsupport.TestSupport.wiktionaryEntry;
import static org.junit.jupiter.api.Assertions.*;

class JsonlResourceReaderTest {

    private final JsonlResourceReader reader = new JsonlResourceReader(new ObjectMapper());

    @Test
    void readIgnoresBlankLines() throws IOException {
        Resource resource = jsonlResource(
                "",
                jsonLine(wiktionaryEntry("sui", "pron")),
                "   "
        );

        List<JsonNode> nodes = new ArrayList<>();
        reader.read(resource, nodes::add);

        assertEquals(1, nodes.size());
        assertEquals("sui", nodes.get(0).path("word").asString());
    }

    @Test
    void readIfPresentReturnsEmptyListGivenMissingResource() throws IOException {
        assertTrue(reader.readIfPresent(null).isEmpty());
    }

    @Test
    void readFailsFastGivenMalformedLine() {
        Resource resource = jsonlResource("{not json");

        assertThrows(JacksonException.class, () -> reader.read(resource, node -> {}));
    }

    @Test
    void readFailsFastGivenUnreadableResource() {
        Resource unreadable = new AbstractResource() {
            @Override
            public String getDescription() {
                return "unreadable JSONL resource";
            }

            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException("cannot read");
            }
        };

        assertThrows(IOException.class, () -> reader.read(unreadable, node -> {}));
    }

    private static Resource jsonlResource(String... lines) {
        byte[] content = String.join("\n", lines).getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(content);
    }
}
