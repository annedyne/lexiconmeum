package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Declension;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

@Component
public class POSNounParser implements PartOfSpeechParser {


    static final Logger logger = LogManager.getLogger(POSNounParser.class);
    ParserSupport parserSupport;

    public POSNounParser(ParserSupport parserSupport) {
        this.parserSupport = parserSupport;
    }

    @Override
    public ParsedResultProcessor parsePartOfSpeech(JsonNode root, POSParserKey parserKey) {
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
        addDeclensionForms(formsNode, lexemeBuilder);

        return new SafeBuilder<>(PartOfSpeech.NOUN.name(), lexemeBuilder::build).build(logger, parserSupport.getParseMode());
    }

    private void addDeclensionForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (parserSupport.isValidFormNode(formNode, PartOfSpeech.NOUN.getInflectionTypeLower())) {
                buildDeclension(formNode, ParseMode.LENIENT).ifPresent(lexemeBuilder::addInflection);
            } else {
                //this sets genders for nouns with gender-specific inflections (Ex: 1st & 2nd, not 3rd)
                setNounCanonicalFormAndGender(formNode, lexemeBuilder);
            }
        }
    }

    Optional<Declension> buildDeclension(JsonNode formNode, ParseMode mode) {
        Declension.Builder builder = new Declension.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            parserSupport.applyToInflection(builder, tag.asText(), logger);
        }

        return new SafeBuilder<>(DECLENSION.get(), builder::build).build(logger, mode);
    }

    // Find and set canonical form and gender of nouns
    void setNounCanonicalFormAndGender(JsonNode formNode, LexemeBuilder lexemeBuilder) {
        JsonNode tags = formNode.path(TAGS.get());
        for (int i = 0; i < tags.size(); i++) {
            //if first tag is CANONICAL then next is gender
            if (CANONICAL.name().equalsIgnoreCase(tags.get(i).asText()) && i + 1 < tags.size()) {
                lexemeBuilder.addCanonicalForm(formNode.path(FORM.get()).asText());
                parserSupport.applyToLexeme(tags.get(i + 1).asText(), lexemeBuilder, logger);
            }
        }
    }

}
