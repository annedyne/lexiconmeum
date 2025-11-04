package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.HEAD_TEMPLATES;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.NAME;

@Component
public class AdjectiveParser implements PartOfSpeechParser {

    public static final String VALID_HEAD_TEMPLATE_NAME = "la-adj";

    @Override
    public boolean validate(JsonNode root) {
        // Only process full adjective structures, not separate form structures
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        String templateName = headTemplates.get(0).path(NAME.get()).asText("");

        return VALID_HEAD_TEMPLATE_NAME.equalsIgnoreCase(templateName);
    }

    @Override
    public void addInflections(LexemeBuilder lexemeBuilder, JsonNode formsNode) {

    }
}
