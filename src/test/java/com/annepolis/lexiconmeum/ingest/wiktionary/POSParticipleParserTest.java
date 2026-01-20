package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class POSParticipleParserTest {

   POSParticipleParser underTest;

   @BeforeEach
   void setUp() {
       underTest = new POSParticipleParser(new LexicalTagResolver());
   }

    @Test
    void isParticipleEntryReturnsTrueGivenParticipleRoot() throws IOException {
        // Simple, readable, and uses the shared instance/cache
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("amans", "testDataVerb.jsonl");

        boolean isParticiple = underTest.validate(root);
        assertTrue(isParticiple);
    }

    @Test
    void isParticipleEntryReturnsFalseGivenAVerbRoot() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("amo", "testDataVerb.jsonl");

        boolean isParticiple = underTest.validate(root);

        assertFalse(isParticiple);
    }

    @Test
    void parseParticipleEntryGeneratesExpectedStagedParticipleDataGivenPresentActiveParticiple() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("amans", "testDataVerb.jsonl");

        StagedParticipleData data = underTest.parseParticipleEntry(root)
                .orElseThrow(() -> new AssertionError("Failed to parse participle entry for 'amans'"));

        assertEquals("amans", data.getParticipleLemma());
        assertEquals("amo", data.getParentLemma());
        assertEquals("ACTIVE|PRESENT", data.getParticipleKey());
        assertEquals("amō", data.getParentLemmaWithMacrons());
    }

    @Test
    void parseParticipleEntryGeneratesExpectedStagedParticipleDataGivenFuturePassiveParticiple() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("amandus", "testDataRaw.jsonl");

        StagedParticipleData data = underTest.parseParticipleEntry(root)
                .orElseThrow(() -> new AssertionError("Failed to parse participle entry for 'amandus'"));

        assertEquals("amandus", data.getParticipleLemma());
        assertEquals("amandus", data.getParentLemma());
        assertEquals("PASSIVE|FUTURE", data.getParticipleKey());
        assertEquals("amandus", data.getParentLemmaWithMacrons());
    }

    @Test
    void parseParticipleInflectionsGeneratesExpectedInflections() throws IOException {
        JsonNode root = JsonTestDataManager.INSTANCE.getRealNode("amans", "testDataVerb.jsonl");

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
    void givenFutureAndNoActive_resolveParticipleTenseTags_addsFutureActiveAndActiveTags(){
         List<String> tags = new ArrayList<>(Arrays.asList( "future",  "declension-1", "declension-2", "form-of", "participle"));
         List<String> processed = underTest.resolveParticipleTenseTags(tags);
         assertTrue(processed.contains("future_active"));   // sets tense
         assertTrue(processed.contains("active"));          // sets voice
    }

}
