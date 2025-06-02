package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.lexeme.detail.Conjugation;
import com.annepolis.lexiconmeum.lexeme.detail.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.InflectionFeature;
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

    static final Logger LOGGER = LogManager.getLogger(WiktionaryLexicalDataParser.class);

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
                LOGGER.error("Check that JSONL is correctly formatted and not 'prettified'", eofException);
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

        JsonNode sensesNode = root.path(SENSES.get());
        if (sensesNode.isArray() && !sensesNode.isEmpty()) {
            for(JsonNode senseNode : sensesNode){
                lexemeBuilder.addSense(buildSense(senseNode));
            }
        }

        JsonNode formsNode = root.path(FORMS.get());
        if (NOUN.get().equalsIgnoreCase(lexemeBuilder.getPosition().name()) && formsNode.isArray()){
            for (JsonNode formNode : formsNode) {
                if (isDeclensionForm(formNode) ) {
                    lexemeBuilder.addInflection(buildDeclension(formNode));
                } else {
                    setGender(formNode, lexemeBuilder);
                }
            }

        } else if (VERB.get().equalsIgnoreCase(lexemeBuilder.getPosition().name()) && formsNode.isArray()){
            lexemeBuilder.setInflectionList(buildConjugationsList(formsNode));
        }

        return lexemeBuilder.build();
    }

    private boolean isDeclensionForm(JsonNode formNode){
        String formValue = formNode.path(FORM.get()).asText();
        return DECLENSION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                && !FORM_BLACKLIST.contains(formValue);
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

    private List<Inflection> buildConjugationsList(JsonNode formsNode){
        List<Inflection> inflections = new ArrayList<>();
        for (JsonNode formNode : formsNode) {
            String formValue = formNode.path(FORM.get()).asText();
            if (!FORM_BLACKLIST.contains(formValue)) {
                inflections.add(buildConjugation(formNode));
            }
        }
        return inflections;
    }

    Inflection buildDeclension(JsonNode formNode){
        Declension.Builder builder = new Declension.Builder(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            InflectionFeature.fromTag(tag.asText()).ifPresent(fc -> fc.applyTo(builder));
        }
        return builder.build();
    }

    Inflection buildConjugation(JsonNode formNode){
        Conjugation inflection = new Conjugation(formNode.path(FORM.get()).asText());

        List<String> tags = new ArrayList<>();
        for (JsonNode tag : formNode.path(TAGS.get())) {
            tags.add(tag.asText());
        }
        return inflection;
    }


}

