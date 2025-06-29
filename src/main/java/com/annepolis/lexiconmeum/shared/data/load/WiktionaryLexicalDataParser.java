package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalTense;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.InflectionFeature;
import com.annepolis.lexiconmeum.lexeme.detail.noun.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.verb.Conjugation;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.Sense;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.shared.data.load.WiktionaryLexicalDataJsonKey.*;

@Component
class WiktionaryLexicalDataParser {

    static final Logger logger = LogManager.getLogger(WiktionaryLexicalDataParser.class);

    private static final Set<String> FORM_BLACKLIST = Set.of(
            "no-table-tags",
            "la-ndecl",
            "conjugation-1",
            "la-conj"
    );

    private final ObjectMapper mapper = new ObjectMapper();

    public void parseJsonl(Reader reader, Consumer<Lexeme> consumer) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = readJsonLine(br)) != null) {
            try {
                JsonNode root = mapper.readTree(line);
                consumer.accept(buildLexeme(root));
            } catch(JsonEOFException eofException) {
                logger.error("Check that JSONL is correctly formatted and not 'prettified'", eofException);
                throw eofException;
            }
        }
    }

    String readJsonLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private Lexeme buildLexeme(JsonNode root) {
        String lemma = root.path(WORD.get()).asText();
        String posTag = root.path(POSITION.get()).asText();

        GrammaticalPosition position = GrammaticalPosition.resolveOrThrow(posTag);
        LexemeBuilder lexemeBuilder = new LexemeBuilder(lemma, position);

        addSenses(root.path(SENSES.get()), lexemeBuilder);
        addForms(root.path(FORMS.get()), lexemeBuilder);

        return lexemeBuilder.build();
    }

    private void addForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        if (!formsNode.isArray()) return;

        String pos = lexemeBuilder.getPosition().name();

        if (NOUN.get().equalsIgnoreCase(pos)) {
            addDeclensionForms(formsNode, lexemeBuilder);
        } else if (VERB.get().equalsIgnoreCase(pos)) {
            addConjugationForms(formsNode, lexemeBuilder);
        }
    }

    private void addDeclensionForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (isDeclensionForm(formNode)) {
                lexemeBuilder.addInflection(buildDeclension(formNode));
            } else {
                setGender(formNode, lexemeBuilder);
            }
        }
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

    private void setGender(JsonNode formNode, LexemeBuilder lexemeBuilder){
        JsonNode tags = formNode.path(TAGS.get());
        for (int i = 0; i < tags.size(); i++) {
            //if first tag is CANONICAL then next is gender
            if (CANONICAL.name().equalsIgnoreCase(tags.get(i).asText()) && i + 1 < tags.size()) {
                lexemeBuilder.setGender(GrammaticalGender.resolveOrThrow(tags.get(i + 1).asText()));
                break;
            }
        }
    }

    private void addSenses(JsonNode sensesNode, LexemeBuilder lexemeBuilder) {
        if (sensesNode.isArray()) {
            for (JsonNode senseNode : sensesNode) {
                lexemeBuilder.addSense(buildSense(senseNode));
            }
        }
    }

    private Sense buildSense(JsonNode senseNode) {
        Sense.Builder builder = new Sense.Builder();

        JsonNode glosses = senseNode.path(GLOSSES.get());
        if (glosses.isArray() && !glosses.isEmpty()) {

            for(JsonNode gloss: glosses){
                builder.addGloss(gloss.asText());
            }
        }
        return builder.build();
    }

    Inflection buildDeclension(JsonNode formNode){
        Declension.Builder builder = new Declension.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            InflectionFeature.resolveOrThrow(tag.asText()).applyTo(builder);

        }
        return builder.build();
    }

    private void addConjugationForms(JsonNode formsNode, LexemeBuilder lexemeBuilder) {
        for (JsonNode formNode : formsNode) {
            if (isConjugationForm(formNode)) {
                try {
                    lexemeBuilder.addInflection(buildConjugation(formNode));
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    logger.warn("Skipping invalid form: {}", ex.getMessage());
                }
            }
        }
    }

    Inflection buildConjugation(JsonNode formNode){
        Conjugation.Builder builder = new Conjugation.Builder(formNode.path(FORM.get()).asText());
        List<String> tags = new ArrayList<>();
        for (JsonNode tag : formNode.path(TAGS.get())) {
            String tagText = tag.asText().toLowerCase();
            tags.add(tagText);
        }
        if(tags.contains(GrammaticalTense.FUTURE.name().toLowerCase()) && tags.contains(GrammaticalTense.PERFECT.name().toLowerCase())){
            tags.remove(GrammaticalTense.FUTURE.name().toLowerCase());
            tags.remove(GrammaticalTense.PERFECT.name().toLowerCase());
            tags.add(GrammaticalTense.FUTURE_PERFECT.name().toLowerCase());
        }
        for (String tag : tags){
            InflectionFeature.resolveOrThrow(tag).applyTo(builder);
        }

        return builder.build();
    }
}

