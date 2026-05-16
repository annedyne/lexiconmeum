package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalDegree;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDegreeAgreementSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveDetails;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.AdjectiveTerminationType;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;
import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataKeyWord.FORM_OF;

@Component
public class POSAdjectiveParser implements PartOfSpeechParser {
    private static final Logger logger = LogManager.getLogger(POSAdjectiveParser.class);
    ParserSupport parserSupport;

    public POSAdjectiveParser(ParserSupport parserSupport) {
        this.parserSupport = parserSupport;
    }

    @Override
    public ParsedResultProcessor parsePartOfSpeech(JsonNode root, POSParserKey parserKey) {

        return switch (parserKey) {
            case ADJECTIVE_POSITIVE -> stageLexeme(root);
            case ADJECTIVE_COMPARATIVE, ADJECTIVE_SUPERLATIVE -> stageAdjective(root);
            case DETERMINER, PRONOUN -> processImmediately(root);
            default -> processImmediately(root);
        };
    }

    private ParsedResultProcessor processImmediately(JsonNode root){
        return parserSupport.initLexemeBuilderFromRoot(root, logger)
                .flatMap(lexemeBuilder -> buildLexeme(lexemeBuilder, root))
                .map(lexeme -> (ParsedResultProcessor) (
                        lexemeConsumer,
                        stagingService) -> lexemeConsumer.accept(lexeme)
                )
                .orElse(ParsedResultProcessor.EMPTY);
    }

    private ParsedResultProcessor stageLexeme(JsonNode root){
        return parserSupport.initLexemeBuilderFromRoot(root, logger)
                .flatMap(lexemeBuilder -> buildLexeme(lexemeBuilder, root))
                .map(lexeme -> (ParsedResultProcessor) (
                        lexemeConsumer,
                        stagingService) -> stagingService.stageLexeme(lexeme)
                )
                .orElse(ParsedResultProcessor.EMPTY);
    }

    private  ParsedResultProcessor stageAdjective(JsonNode root){
        // Build comparative or superlative as LinkableData and return wrapped in the appropriate processor, or EMPTY if no result
        return parseDegreeForms(root).map(adjectiveDegreeData -> (ParsedResultProcessor) (
                        lexemeConsumer,
                        stagingService) -> stagingService.stageLinkableData(adjectiveDegreeData)
                )
                .orElse(ParsedResultProcessor.EMPTY);
    }

    private Optional<Lexeme> buildLexeme(LexemeBuilder lexemeBuilder, JsonNode root){
        // Initialize AdjectiveDetails so initialization is independent
        // of tag data. If the termination-type sense tag is present, it will
        // override the default NONE.
        if(lexemeBuilder.getPartOfSpeechDetails() == null){
            AdjectiveDetails.Builder adBuilder = new AdjectiveDetails.Builder();
            adBuilder.setAdjectiveTerminationType(AdjectiveTerminationType.NONE);
            lexemeBuilder.setPartOfSpeechDetails(adBuilder.build());
        }
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
        if(!parserSupport.isValidForm(formNode)) {
            return false;
        }
        if (!formNode.has(SOURCE.get())) {
            return false;
        }
        String source = formNode.path(SOURCE.get()).asText();

        // wrong inflection table header or pos in wiktionary data
        String formValue = formNode.path(FORM.get()).asText();

        if (!DECLENSION.get().equalsIgnoreCase(source) && !INFLECTION.get().equalsIgnoreCase(source)){
            logger.trace(ParserSupport.LogMsg.UNEXPECTED_INFLECTION_SOURCE, source, formValue);
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

    Optional<StagedAdjectiveDegreeData> parseDegreeForms(JsonNode root) {
        String lemma = root.path(WORD.get()).asText();

        // Build Lexeme from root of adjective degree
        Optional<Lexeme> optionalLexeme = parserSupport.initLexemeBuilderFromRoot(root, logger)
                .flatMap(lexemeBuilder -> buildLexeme(lexemeBuilder, root));
        if(optionalLexeme.isEmpty()){
            return Optional.empty();
        }
        Lexeme lexeme = optionalLexeme.get();

        // Extract parent information from sense node form_of tag if it exists,
        // NB: Assuming the same tags for all senses for now
        JsonNode senseNode = root.path(SENSES.get()).get(0);

        // extract info for linking to positive degree form
        Optional<String> optionalParentLemmaWithMacrons = extractParentLemma(senseNode.path(FORM_OF.get()));
        if(optionalParentLemmaWithMacrons.isEmpty()){
            return Optional.empty();
        }
        String parentLemmaWithMacrons = optionalParentLemmaWithMacrons.get();
        String parentLemma = parserSupport.normalizeDiacritics(parentLemmaWithMacrons);

        // get the degree of the given adjective
        Optional<GrammaticalDegree> optionalGrammaticalDegree = extractGrammaticalDegreeFromSenseNode(senseNode);
        if(optionalGrammaticalDegree.isEmpty()){
            return Optional.empty();
        }

        // Populate model
        AdjectiveDegreeAgreementSet agreementSet = new AdjectiveDegreeAgreementSet(lemma, optionalGrammaticalDegree.get(), lexeme.getInflectionClasses());
        agreementSet.setInflectionIndex(lexeme.getInflectionIndex());

        // Wrap model as linkable data
        StagedAdjectiveDegreeData degreeData = new StagedAdjectiveDegreeData(parentLemma, parentLemmaWithMacrons, agreementSet );
        return Optional.of(degreeData);
    }

    // extract parent from formOf node
    Optional<String> extractParentLemma(JsonNode formOfArray){
        if (formOfArray != null && !formOfArray.isEmpty()) {
            String text = formOfArray.get(0).path(WORD.get()).asText();
            return !text.isEmpty() ? Optional.of(text) : Optional.empty();
        }

        return Optional.empty();
    }

    // resolve all tags into dummy Agreement builder and extract degree from builder
    Optional<GrammaticalDegree> extractGrammaticalDegreeFromSenseNode(JsonNode senseNode){
        List<String> senseTags = parserSupport.collectTags(senseNode);
        Agreement.Builder agreementBuilder = new Agreement.Builder("dummy");
        parserSupport.applyAllToInflection(senseTags, agreementBuilder, logger);
        GrammaticalDegree degree = agreementBuilder.build().getDegree();
        return degree != null ? Optional.of(degree) : Optional.empty();
    }
}
