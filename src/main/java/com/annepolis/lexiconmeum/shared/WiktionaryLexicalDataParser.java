package com.annepolis.lexiconmeum.shared;

import com.annepolis.lexiconmeum.textsearch.Inflection;
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

import static com.annepolis.lexiconmeum.shared.WiktionaryLexicalDataJsonKey.*;

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
                consumer.accept(parseWord(root));
            } catch(JsonEOFException eofException) {
                LOGGER.error("Check that JSONL is correctly formatted and not 'prettified'", eofException);
                throw eofException;
            }
        }
    }

    String readJsonLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private Lexeme parseWord(JsonNode root) {
        Lexeme lexeme = new Lexeme();
        lexeme.setLemma(root.path(WORD.get()).asText());
        lexeme.setPosition(root.path(POSITION.get()).asText());

        JsonNode senses = root.path(SENSES.get());
        if (senses.isArray() && senses.isEmpty()) {
            JsonNode firstSense = senses.get(0);
            JsonNode glosses = firstSense.path(GLOSSES.get());
            if (glosses.isArray() && glosses.isEmpty()) {
                lexeme.setDefinition(glosses.get(0).asText());
            }
        }

        List<Inflection> inflections = new ArrayList<>();
        JsonNode formsNode = root.path(FORMS.get());

        if (NOUN.get().equalsIgnoreCase(lexeme.getPosition()) && formsNode.isArray()){
            inflections = parseDeclensions(formsNode);
        } else if (VERB.get().equalsIgnoreCase(lexeme.getPosition()) && formsNode.isArray()){
            inflections = parseConjugations(root);
        }

        lexeme.setInflections(inflections);
        return lexeme;
    }

    private List<Inflection> parseConjugations(JsonNode root){
        List<Inflection> inflections = new ArrayList<>();
        for (JsonNode formNode : root.path(FORMS.get())) {
            String formValue = formNode.path(FORM.get()).asText();
            if(!FORM_BLACKLIST.contains(formValue)) {
                Inflection inf = new Inflection();
                inf.setInflection(formNode.path(FORM.get()).asText());

                List<String> tags = new ArrayList<>();
                for (JsonNode tag : formNode.path(TAGS.get())) {
                    tags.add(tag.asText());
                }
                inf.setTags(tags);

                inflections.add(inf);
            }
        }

        return inflections;
    }

    private List<Inflection> parseDeclensions(JsonNode formsNode){
        List<Inflection> inflections = new ArrayList<>();
            for (JsonNode formNode : formsNode) {
                String formValue = formNode.path(FORM.get()).asText();

                boolean isDeclension = DECLENSION.get().equalsIgnoreCase(formNode.path(SOURCE.get()).asText())
                        && !FORM_BLACKLIST.contains(formValue);

                if (isDeclension) {
                    Inflection inf = new Inflection();
                    inf.setInflection(formValue);

                    List<String> tags = new ArrayList<>();
                    for (JsonNode tag : formNode.path(TAGS.get())) {
                        tags.add(tag.asText());
                    }
                    inf.setTags(tags);

                    inflections.add(inf);
                }
            }


        return inflections;
    }

}

