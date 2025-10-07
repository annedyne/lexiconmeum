package com.annepolis.lexiconmeum.ingest.wiktionary;

import com.annepolis.lexiconmeum.ingest.tagmapping.LexicalTagResolver;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.inflection.Agreement;
import com.annepolis.lexiconmeum.shared.model.inflection.Conjugation;
import com.annepolis.lexiconmeum.shared.model.inflection.Declension;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.ingest.wiktionary.WiktionaryLexicalDataJsonKey.*;

@Component
class WiktionaryLexicalDataParser {

    static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataParser.class);

    private static final Set<String> FORM_BLACKLIST = Set.of(
            "no-table-tags",
            "la-ndecl",
            "conjugation-1",
            "la-conj",
            "la-adecl",
            "two-termination",
            "sigmatic"
    );

    private static final Set<String> TAG_BLACKLIST = Set.of(
            "sigmatic"
    );

    private final ObjectMapper mapper = new ObjectMapper();
    private ParseMode parseMode;

    // Inject the resolver instead of instantiating it
    private final LexicalTagResolver lexicalTagResolver;

    public ParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ParseMode parseMode) {
        this.parseMode = parseMode;
    }

    WiktionaryLexicalDataParser(LexicalTagResolver lexicalTagResolver) {
        this.lexicalTagResolver = lexicalTagResolver;
    }

    public void parseJsonl(Reader reader, Consumer<Lexeme> consumer) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = readJsonLine(br)) != null) {
            try {
                JsonNode root = mapper.readTree(line);
                buildLexeme(root).ifPresent(consumer);
            } catch(JsonEOFException eofException) {
                logger.error("Check that JSONL is correctly formatted and not 'prettified'", eofException);
                throw eofException;
            }
        }
    }

    String readJsonLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private Optional<Lexeme> buildLexeme(JsonNode root) {
        String lemma = root.path(WORD.get()).asText();
        String posTag = root.path(PART_OF_SPEECH.get()).asText();
        String etymologyNumber = normalizeEtymologyNumber(root.path(ETYMOLOGY_NUMBER.get()).asText());

        Optional<PartOfSpeech> optionalPartOfSpeech = PartOfSpeech.resolveWithWarning(posTag, logger);

        if (optionalPartOfSpeech.isEmpty()) {
            return Optional.empty();
        }
        PartOfSpeech partOfSpeech = optionalPartOfSpeech.get();

        LexemeBuilder builder = new LexemeBuilder(lemma, partOfSpeech, etymologyNumber);

        addSenses(root.path(SENSES.get()), builder);

        return switch (partOfSpeech) {
            case NOUN -> buildLexemeWithForms(builder, root, this::addDeclensionForms);
            case VERB -> buildLexemeWithForms(builder, root, this::addConjugationForms);
            case ADJECTIVE -> buildLexemeWithForms(builder, root, this::addAdjectiveForms);
            case ADVERB, PREPOSITION, POSTPOSITION -> buildLexemeWithOutForms(builder);
            default -> {
                logger.trace("Unsupported partOfSpeech: {}", partOfSpeech);
                yield Optional.empty();
            }
        };
    }

    public static String normalizeEtymologyNumber(String ety) {
        return ety == null || ety.isBlank() ? "1" : ety;
    }

    private Optional<Lexeme> buildLexemeWithOutForms(LexemeBuilder builder){
        try {
            return Optional.of(builder.build());
        } catch (Exception ex) {
            logger.warn("Failed to build lexeme: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Lexeme> buildLexemeWithForms(
            LexemeBuilder builder,
            JsonNode root,
            BiConsumer<JsonNode, LexemeBuilder> addForms
    ) {
        addForms.accept(root.path(FORMS.get()), builder);
        try {
            return Optional.of(builder.build());
        } catch (Exception ex) {
            logger.warn("Failed to build lexeme: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private void addAdjectiveForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if(isDeclensionForm(formNode)) {
                try {
                    lexemeBuilder.addInflection(buildAgreement(formNode));
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    logger.trace("Skipping invalid form: {}", ex.getMessage());
                }
            }

        }
    }


    private void addDeclensionForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (isDeclensionForm(formNode)) {
                buildDeclension(formNode, getParseMode()).ifPresent(lexemeBuilder::addInflection);
            } else {
                //this sets genders for nouns with gender-specific inflections (Ex: 1st & 2nd, not 3rd)
                setCanonicalGender(formNode, lexemeBuilder);
            }
        }
    }
    //finds
    void setCanonicalGender(JsonNode formNode, LexemeBuilder lexemeBuilder){
        JsonNode tags = formNode.path(TAGS.get());
        for (int i = 0; i < tags.size(); i++) {
            //if first tag is CANONICAL then next is gender
            if (CANONICAL.name().equalsIgnoreCase(tags.get(i).asText()) && i + 1 < tags.size()) {

                // Use LexicalTagResolver instead of direct factory access
                lexicalTagResolver.applyToLexeme(tags.get(i + 1).asText(), lexemeBuilder, logger);
            }
        }
    }

    private void addConjugationForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (isConjugationForm(formNode)) {
                try {
                    buildConjugation(formNode).ifPresent(lexemeBuilder::addInflection);
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    logger.trace("Skipping invalid form: {}", ex.getMessage());
                }
            }
        }
    }

    Agreement buildAgreement(JsonNode formNode) {
        Agreement.Builder builder = new Agreement.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            lexicalTagResolver.applyToInflection(tag.asText(), builder, logger);
        }

        return builder.build();
    }

    Optional<Declension> buildDeclension(JsonNode formNode, ParseMode mode) {
        Declension.Builder builder = new Declension.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            lexicalTagResolver.applyToInflection(tag.asText(), builder, logger);
        }

        return new SafeBuilder<>("Declension", builder::build).build(logger, mode);
    }

    Optional<Conjugation> buildConjugation(JsonNode formNode){

        boolean hasBlacklisted = false;
        for (JsonNode t : formNode.path(TAGS.get())) {
            if (TAG_BLACKLIST.contains(t.asText())) { hasBlacklisted = true; break; }
        }
        if (hasBlacklisted){
            return Optional.empty();
        }

        Conjugation.Builder builder = new Conjugation.Builder(formNode.path(FORM.get()).asText());

        boolean seenFuture = false;
        boolean seenPerfect = false;

        for (JsonNode tagNode : formNode.path(TAGS.get())) {
            String tag = tagNode.asText();

            if (GrammaticalTense.FUTURE.name().equalsIgnoreCase(tag)) {
                seenFuture = true;
            } else if (GrammaticalTense.PERFECT.name().equalsIgnoreCase(tag)) {
                seenPerfect = true;
            } else {
                // Apply non-compound-tense tags immediately
                lexicalTagResolver.applyToInflection(tag, builder, logger);
            }
        }

        // Transform Future and Perfect tag combo to compound tense
        if (seenFuture && seenPerfect) {
            lexicalTagResolver.applyToInflection(GrammaticalTense.FUTURE_PERFECT.name(), builder, logger);
        } else {
            if (seenFuture) {
                lexicalTagResolver.applyToInflection(GrammaticalTense.FUTURE.name(), builder, logger);
            }
            if (seenPerfect) {
                lexicalTagResolver.applyToInflection(GrammaticalTense.PERFECT.name(), builder, logger);
            }
        }

        return Optional.of(builder.build());
    }

    private boolean isDeclensionForm(JsonNode formNode){
        String formValue = formNode.path(FORM.get()).asText();

        return DECLENSION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                && !FORM_BLACKLIST.contains(formValue);
    }

    private boolean isConjugationForm(JsonNode formNode){
        String formValue = formNode.path(FORM.get()).asText();

        return CONJUGATION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                && !FORM_BLACKLIST.contains(formValue)
                && !formValue.contains("+"); //for passive of compound tenses, wiktionary
                                             // doesn't include person so excluding for now
    }

    private void addSenses(JsonNode sensesNode, LexemeBuilder lexemeBuilder) {
        if (sensesNode.isArray()) {
            for (JsonNode senseNode : sensesNode) {
                lexemeBuilder.addSense(buildSense(senseNode, lexemeBuilder));
            }
        }
    }

    private Sense buildSense(JsonNode senseNode, LexemeBuilder lexemeBuilder) {
        Sense.Builder builder = new Sense.Builder();
        JsonNode tags = senseNode.path(TAGS.get());
        if(tags.isArray() && !tags.isEmpty()){
            for(JsonNode tag : tags){
                // Route all sense-level tags through the facade
                lexicalTagResolver.applyToLexeme(tag.asText(), lexemeBuilder, logger);
            }
        }

        JsonNode glosses = senseNode.path(GLOSSES.get());
        if (glosses.isArray() && !glosses.isEmpty()) {

            for(JsonNode gloss: glosses){
                builder.addGloss(gloss.asText());
            }
        }
        return builder.build();
    }


}
