package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.TestUtil;
import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.WORD;
import static org.junit.jupiter.api.Assertions.*;

public class ParticipleParserTest {

   POSParticipleParser underTest;

   @BeforeEach
   void setUp() {
       underTest = new POSParticipleParser(new LexicalTagResolver());
   }

    @Test
    void isParticipleEntryReturnsTrueGivenParticipleRoot() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amans"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participle 'amans' not found"));
        POSParticipleParser underTest = new POSParticipleParser(new LexicalTagResolver());
        boolean isParticiple = underTest.isValidParticipleEntry(root);

        assertTrue(isParticiple);
    }

    @Test
    void isParticipleEntryReturnsFalseGivenAVerbRoot() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Verb 'amo' not found"));

        boolean isParticiple = underTest.isValidParticipleEntry(root);

        assertFalse(isParticiple);
    }

    @Test
    void parseParticipleEntryGeneratesExpectedStagedParticipleDataGivenPresentActiveParticiple() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amans"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participle 'amans' not found"));

        StagedParticipleData data = underTest.parseParticipleEntry(root);

        assertEquals("amans", data.getParticipleLemma());
        assertEquals("amo", data.getParentLemma());
        assertEquals("ACTIVE|PRESENT", data.getParticipleKey());
        assertEquals("amō", data.getParentLemmaWithMacrons());
    }

    @Test
    void parseParticipleEntryGeneratesExpectedStagedParticipleDataGivenFuturePassiveParticiple() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amandus"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participle 'amandus' not found"));

        StagedParticipleData data = underTest.parseParticipleEntry(root);

        assertEquals("amandus", data.getParticipleLemma());
        assertEquals("amandus", data.getParentLemma());
        assertEquals("PASSIVE|FUTURE", data.getParticipleKey());
        assertEquals("amandus", data.getParentLemmaWithMacrons());
    }

    @Test
    void parseParticipleInflectionsGeneratesExpectedInflections() throws IOException {
        JsonNode root = TestUtil.getJsonRootNodes().stream()
                .filter(node -> node.path(WORD.get()).asText().equals("amans"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participle 'amans' not found"));

        List<Participle> inflections = underTest.parseParticipleInflections(root);

        //Spot Check is fine, since props set via Agreement tagMapping
        assertEquals(18, inflections.size());
        Participle ablativePlural = inflections.stream()
                .filter(p -> p.getGrammaticalCase() == GrammaticalCase.ABLATIVE
                        && p.getNumber() == GrammaticalNumber.PLURAL
                        && p.getGenders().size() == 3)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ablative plural not found"));

        assertEquals(3,ablativePlural.getGenders().size() );
        assertEquals("amantibus",ablativePlural.getForm() );
        assertEquals(GrammaticalCase.ABLATIVE,ablativePlural.getGrammaticalCase() );
    }

    @Test
    void removeMacronsNormalizesStringAsExpected(){
        String normalized = underTest.removeMacrons("āēīōū");
        assertEquals("aeiou", normalized);
    }
}
