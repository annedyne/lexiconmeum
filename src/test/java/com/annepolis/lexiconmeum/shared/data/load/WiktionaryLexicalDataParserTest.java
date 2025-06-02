package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.Lexeme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = WiktionaryLexicalDataParser.class)
class WiktionaryLexicalDataParserTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WiktionaryLexicalDataParser parser;

    @Test
    void resourceExists() {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");

        assertTrue(resource.exists(), "Expected testDataRaw.jsonl to be present on the classpath");
    }

    @Test
    void nounResourceExists() {
        Resource resource = resourceLoader.getResource("classpath:testDataNoun.jsonl");

        assertTrue(resource.exists(), "Expected testDataNoun.jsonl to be present on the classpath");
    }

    /**
     * Using the parser to test the json file
     */
    @Test
    void JsonlFileParsesWithoutError()  {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        assertDoesNotThrow(() -> {
            try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                BufferedReader br = new BufferedReader(reader);
                parser.readJsonLine(br);
            }
        });
    }

    @Test
    void testLoadJsonFile() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);

            assertEquals(3, lexemes.size());
            assertEquals(GrammaticalPosition.VERB, lexemes.get(0).getPosition());
            assertEquals(GrammaticalPosition.NOUN, lexemes.get(1).getPosition());
        }
    }

    @Test
    void testLoadWord() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataRaw.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);
            assertEquals("amo", lexemes.get(0).getLemma());

        }
    }

    @Test
    void testLoadNoun() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataNoun.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);
            assertEquals("poculum", lexemes.get(0).getLemma());

        }
    }

    @Test
    void inflectionsAreLoaded() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:testDataNoun.jsonl");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            List<Lexeme> lexemes = new ArrayList<>();
            parser.parseJsonl(reader, lexemes::add);
            assertEquals("pōculum", lexemes.get(0).getInflections().get(0).toString());

        }
    }


}
