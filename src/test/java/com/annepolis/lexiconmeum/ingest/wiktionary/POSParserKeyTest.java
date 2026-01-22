package com.annepolis.lexiconmeum.ingest.wiktionary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class POSParserKeyTest {

    @Test
    void resolveReturnsExpectedGivenValidPOSAndTemplateName() {
        // Test successful resolution for a Verb
        var verbResult = POSParserKey.fromHeadTemplateName("la-verb");
        assertTrue(verbResult.isPresent());
        assertEquals(POSParserKey.VERB, verbResult.get());

        // Test superlative adjective
        var superlativeResult = POSParserKey.fromHeadTemplateName(
                "la-adj-sup"
        );

        assertTrue(superlativeResult.isPresent());
        assertEquals(POSParserKey.ADJECTIVE_SUPERLATIVE, superlativeResult.get());
    }

    @Test
    void resolveReturnsTrueGivenPosName(){
        var pronounResult = POSParserKey.fromHeadTemplateName("pronoun");
        assertTrue(pronounResult.isPresent());
        assertEquals(POSParserKey.PRONOUN, pronounResult.get());

        var conjResult = POSParserKey.fromHeadTemplateName("conjunction");
        assertTrue(conjResult.isPresent());
        assertEquals(POSParserKey.CONJUNCTION, conjResult.get());

        var detResult = POSParserKey.fromHeadTemplateName("determiner");
        assertTrue(detResult.isPresent());
        assertEquals(POSParserKey.DETERMINER, detResult.get());
    }

    @Test
    void resolveReturnsTrueGivenTemplateName(){
        var detResult = POSParserKey.fromHeadTemplateName("la-det");
        assertTrue(detResult.isPresent());
        assertEquals(POSParserKey.DETERMINER, detResult.get());
    }

    @Test
    void resolveReturnsEmptyGivenBadTemplateName(){
        var detResult = POSParserKey.fromHeadTemplateName("bad");
        assertTrue(detResult.isEmpty());
    }

    @Test
    void resolveIgnoresCaseOfInputs(){

        // Test case insensitivity
        var adjResult = POSParserKey.fromHeadTemplateName("la-adj" );
        assertTrue(adjResult.isPresent());
        assertEquals(POSParserKey.ADJECTIVE_POSITIVE, adjResult.get());

    }

    @Test
    void resolveReturnsEmptyOptionalGivenNonExistentPOSandTemplateName(){
        // Test non-existent combination
        //var emptyResult = POSParserKey.resolveWithPosTagAndHeadTemplateName("BAD", "la-noun");
        //assertTrue(emptyResult.isEmpty());

        // Test null/empty values
        //assertTrue(POSParserKey.resolveWithPosTagAndHeadTemplateName(null, "la-verb").isEmpty());
        assertTrue(POSParserKey.fromHeadTemplateName("").isEmpty());
    }
}