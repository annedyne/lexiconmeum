package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class POSParserKeyTest {

    @Test
    void resolveReturnsExpectedGivenValidPOSAndTemplateName() {
        // Test successful resolution for a Verb
        var verbResult = POSParserKey.resolveWithPosTagAndHeadTemplateName("VERB", "la-verb");
        assertTrue(verbResult.isPresent());
        assertEquals(POSParserKey.VERB, verbResult.get());

        // Test superlative adjective
        var superlativeResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(
                PartOfSpeech.ADJECTIVE.getTag().toLowerCase(),
                "la-adj-sup"
        );

        assertTrue(superlativeResult.isPresent());
        assertEquals(POSParserKey.ADJECTIVE_SUPERLATIVE, superlativeResult.get());
    }

    @Test
    void resolveReturnsTrueGivenPosName(){
        var pronounResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(PartOfSpeech.PRONOUN.getTag(), "pronoun");
        assertTrue(pronounResult.isPresent());
        assertEquals(POSParserKey.PRONOUN, pronounResult.get());

        var conjResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(PartOfSpeech.CONJUNCTION.getTag(), "conjunction");
        assertTrue(conjResult.isPresent());
        assertEquals(POSParserKey.CONJUNCTION, conjResult.get());

        var detResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(PartOfSpeech.DETERMINER.getTag(), "determiner");
        assertTrue(detResult.isPresent());
        assertEquals(POSParserKey.DETERMINER, detResult.get());
    }

    @Test
    void resolveReturnsTrueGivenTemplateName(){
        var detResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(PartOfSpeech.DETERMINER.getTag(), "la-det");
        assertTrue(detResult.isPresent());
        assertEquals(POSParserKey.DETERMINER, detResult.get());
    }

    @Test
    void resolveReturnsEmptyGivenBadTemplateName(){
        var detResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(PartOfSpeech.DETERMINER.getTag(), "bad");
        assertTrue(detResult.isEmpty());
    }

    @Test
    void resolveIgnoresCaseOfInputs(){

        // Test case insensitivity
        var adjResult = POSParserKey.resolveWithPosTagAndHeadTemplateName(PartOfSpeech.ADJECTIVE.getTag(),"la-adj" );
        assertTrue(adjResult.isPresent());
        assertEquals(POSParserKey.ADJECTIVE_POSITIVE, adjResult.get());

    }

    @Test
    void resolveReturnsEmptyOptionalGivenNonExistentPOSandTemplateName(){
        // Test non-existent combination
        var emptyResult = POSParserKey.resolveWithPosTagAndHeadTemplateName("BAD", "la-noun");
        assertTrue(emptyResult.isEmpty());

        // Test null/empty values
        assertTrue(POSParserKey.resolveWithPosTagAndHeadTemplateName(null, "la-verb").isEmpty());
        assertTrue(POSParserKey.resolveWithPosTagAndHeadTemplateName("VERB", "").isEmpty());
    }
}