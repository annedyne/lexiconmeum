package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

@Component
public class POSAdjectiveParser implements PartOfSpeechParser {
    private static final Logger logger = LogManager.getLogger(POSAdjectiveParser.class);

    @Override
    public boolean isActive() {
        return true;
    }

    ParserSupport parserSupport;
    public POSAdjectiveParser(ParserSupport parserSupport) {
        this.parserSupport = parserSupport;
    }
    @Override
    public ParsedResultProcessor parsePartOfSpeech(JsonNode root) {
        // Build the lexeme and return it wrapped in the appropriate processor, or EMPTY if no result
        return parserSupport.initLexemeBuilderFromRoot(root, logger)
                .flatMap(lexemeBuilder -> buildLexeme(lexemeBuilder, root))
                .map(lexeme -> (ParsedResultProcessor) (
                        lexemeConsumer,
                        stagingService) -> lexemeConsumer.accept(lexeme)
                )
                .orElse(ParsedResultProcessor.EMPTY);
    }

    private Optional<Lexeme> buildLexeme(LexemeBuilder lexemeBuilder, JsonNode root){

        parserSupport.addSenses(root.path(SENSES.get()), lexemeBuilder, logger);

        JsonNode formsNode = root.path(FORMS.get());
        addAdjectiveForms(formsNode, lexemeBuilder);

        return new SafeBuilder<>(PartOfSpeech.ADJECTIVE.name(), lexemeBuilder::build).build(logger, parserSupport.getParseMode());

    }

    private void addAdjectiveForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if(isDeclensionForm(formNode)) {
                Agreement agreement = buildAgreement(formNode);
                lexemeBuilder.addInflection(agreement);
            } else {
                parserSupport.addCanonicalForm(formNode, lexemeBuilder);
            }
        }
    }

    // Filter out form nodes in black-list
    private boolean isDeclensionForm(JsonNode formNode){
        if(!parserSupport.isValidFormNode(formNode, PartOfSpeech.ADJECTIVE.getInflectionType())) {
            return false;
        }
        String source = formNode.path(SOURCE.get()).asText();

        // wrong inflection table header or pos in wiktionary data
        String formValue = formNode.path(FORM.get()).asText();

        if (!DECLENSION.get().equalsIgnoreCase(source) && !INFLECTION.get().equalsIgnoreCase(source)){
            logger.trace(WiktionaryLexicalDataParser.LogMsg.UNEXPECTED_INFLECTION_SOURCE, source, formValue);
        }
        return true;
    }

    // Build adjective inflected form
    Agreement buildAgreement(JsonNode formNode) {
        Agreement.Builder builder = new Agreement.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            parserSupport.applyToInflection(builder, tag.asText(), logger);
        }

        return builder.build();
    }
}
