package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.HEAD_TEMPLATES;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.NAME;

@Component
public class POSAdjectiveParser implements PartOfSpeechParser {

    private static final Set<String> VALID_TEMPLATE_NAMES = Set.of(
            WiktionaryHeadTemplate.ADJECTIVE_POSITIVE.getName(),
            WiktionaryHeadTemplate.ADJECTIVE_COMPARATIVE.getName(),
            WiktionaryHeadTemplate.ADJECTIVE_SUPERLATIVE.getName()
    );

    @Override
    public boolean validate(JsonNode root) {
        // Only process full adjective structures, not separate form structures
        JsonNode headTemplates = root.path(HEAD_TEMPLATES.get());
        if (headTemplates.isMissingNode() || headTemplates.isEmpty()) {
            return false;
        }

        String templateName = headTemplates.get(0).path(NAME.get()).asText("").toLowerCase();

        return VALID_TEMPLATE_NAMES.contains(templateName);
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
