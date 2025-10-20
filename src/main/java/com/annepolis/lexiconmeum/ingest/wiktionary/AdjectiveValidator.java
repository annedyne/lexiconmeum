package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.HEAD_TEMPLATES;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.NAME;

@Component
public class AdjectiveValidator implements PartOfSpeechValidator{

    public static final String VALID_HEAD_TEMPLATE_NAME = "la-adj";

    @Override
    public boolean validate(JsonNode root) {
        // Only process full adjective structures, not separate form structures
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        String templateName = headTemplates.get(0).path(NAME.get()).asText("");

        return VALID_HEAD_TEMPLATE_NAME.equalsIgnoreCase(templateName);
    }
}
