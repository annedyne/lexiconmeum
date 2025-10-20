package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.fasterxml.jackson.databind.JsonNode;

public interface PartOfSpeechValidator {

    boolean validate(JsonNode root);
}
