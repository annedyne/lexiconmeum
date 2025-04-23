package com.annepolis.lexiconmeum.extract;

import com.annepolis.lexiconmeum.model.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = WiktionaryParser.class)
public class JsonLinesProcessorTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WiktionaryParser parser;


    @Test
    void testLoadJsonFile() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Word> words = parser.parseJsonl(reader);

            assertEquals(2, words.size());
            assertEquals("verb", words.get(0).getPosition());
        }
    }


}
