package com.annepolis.lexiconmeum.shared.data.load;

import com.annepolis.lexiconmeum.lexeme.detail.Conjugation;
import com.annepolis.lexiconmeum.lexeme.detail.Declension;
import com.annepolis.lexiconmeum.lexeme.detail.GrammaticalFeature;
import com.annepolis.lexiconmeum.lexeme.detail.Inflection;
import com.annepolis.lexiconmeum.shared.Lexeme;
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
        String position = root.path(POSITION.get()).asText();
        Lexeme lexeme = new Lexeme(lemma, position);

        JsonNode sensesNode = root.path(SENSES.get());
        if (sensesNode.isArray() && !sensesNode.isEmpty()) {
            for(JsonNode senseNode : sensesNode){
                lexeme.addSense(buildSense(senseNode));
            }
        }

        JsonNode formsNode = root.path(FORMS.get());
        if (NOUN.get().equalsIgnoreCase(lexeme.getPosition()) && formsNode.isArray()){
            lexeme.setInflections(buildDeclensionsList(formsNode));
        } else if (VERB.get().equalsIgnoreCase(lexeme.getPosition()) && formsNode.isArray()){
            lexeme.setInflections(buildConjugationsList(formsNode));
        }

        return lexeme;
    }

    private Sense buildSense(JsonNode senseNode) {
        Sense sense = new Sense();
        JsonNode glosses = senseNode.path(GLOSSES.get());
        if (glosses.isArray() && !glosses.isEmpty()) {

            for(JsonNode gloss: glosses){
                sense.addGloss(gloss.asText());
            }
        }
        return sense;
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

    private List<Inflection> buildDeclensionsList(JsonNode formsNode){
        List<Inflection> inflections = new ArrayList<>();
            for (JsonNode formNode : formsNode) {
                String formValue = formNode.path(FORM.get()).asText();
                boolean isDeclension = DECLENSION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                        && !FORM_BLACKLIST.contains(formValue);
                if (isDeclension) {
                    inflections.add(buildDeclension(formNode));
                }
            }

        return inflections;
    }

    Inflection buildDeclension(JsonNode formNode){
        Declension inflection = new Declension();
        inflection.setForm(formNode.path(FORM.get()).asText());

        for (JsonNode tag : formNode.path(TAGS.get())) {
            GrammaticalFeature.fromTag(tag.asText()).ifPresent(fc -> fc.applyTo(inflection));
        }
        return inflection;
    }

    Inflection buildConjugation(JsonNode formNode){
        Conjugation inflection = new Conjugation(formNode.path(FORM.get()).asText());

        List<String> tags = new ArrayList<>();
        for (JsonNode tag : formNode.path(TAGS.get())) {
            tags.add(tag.asText());
        }
        inflection.setTags(tags);
        return inflection;
    }


}

