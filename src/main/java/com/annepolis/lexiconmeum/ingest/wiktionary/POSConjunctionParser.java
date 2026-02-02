package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.SENSES;

@Component
public class POSConjunctionParser implements PartOfSpeechParser{

    private static final Logger logger = LogManager.getLogger(POSConjunctionParser.class);

    ParserSupport parserSupport;

    public POSConjunctionParser(ParserSupport parserSupport) {
        this.parserSupport = parserSupport;
    }

    @Override
    public boolean isActive() {
        return true;
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

        parserSupport.extractCanonicalForms(root, lexemeBuilder);
        parserSupport.addSenses(root.path(SENSES.get()), lexemeBuilder, logger);

        return new SafeBuilder<>(PartOfSpeech.CONJUNCTION.name(),
                lexemeBuilder::build).build(logger,
                parserSupport.getParseMode()
        );
    }

}
