package com.annepolis.lexiconmeum.ingest.tagmapping;

import com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryHeadTemplate;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class POSParserKeyTest {

    @Test
    void resolveReturnsExpectedGivenValidPOSAndTemplateName() {
        // Test successful resolution for a Verb
        var verbResult = POSParserKey.resolve("VERB", "la-verb");
        assertTrue(verbResult.isPresent());
        assertEquals(POSParserKey.VERB, verbResult.get());

        // Test superlative adjective
        var superlativeResult = POSParserKey.resolve(
                PartOfSpeech.ADJECTIVE.name().toLowerCase(),
                WiktionaryHeadTemplate.ADJECTIVE_SUPERLATIVE.getName().toLowerCase()
        );

        assertTrue(superlativeResult.isPresent());
        assertEquals(POSParserKey.ADJECTIVE_SUPERLATIVE, superlativeResult.get());
    }

    @Test
    void resolveIgnoresCaseOfInputs(){

        // Test case insensitivity
        var adjResult = POSParserKey.resolve(PartOfSpeech.ADJECTIVE.name(), WiktionaryHeadTemplate.ADJECTIVE_POSITIVE.getName() );
        assertTrue(adjResult.isPresent());
        assertEquals(POSParserKey.ADJECTIVE_POSITIVE, adjResult.get());

    }

    @Test
    void resolveReturnsEmptyOptionalGivenNonExistantPOSandTemplateName(){
        // Test non-existent combination
        var emptyResult = POSParserKey.resolve("NOUN", "la-noun");
        assertTrue(emptyResult.isEmpty());

        // Test null/empty values
        assertTrue(POSParserKey.resolve(null, "la-verb").isEmpty());
        assertTrue(POSParserKey.resolve("VERB", "").isEmpty());
    }
}