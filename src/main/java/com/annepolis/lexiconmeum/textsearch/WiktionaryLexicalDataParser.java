package com.annepolis.lexiconmeum.textsearch;

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
import java.util.function.Consumer;

import static com.annepolis.lexiconmeum.textsearch.WiktiionaryLexicalDataJsonKey.*;

@Component
class WiktionaryLexicalDataParser {

    static final Logger LOGGER = LogManager.getLogger(WiktionaryLexicalDataParser.class);


    private final ObjectMapper mapper = new ObjectMapper();

    public void parseJsonl(Reader reader, Consumer<Word> consumer) throws IOException {
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

    private Word parseWord(JsonNode root) {
        Word word = new Word();
        word.setWord(root.path(WORD.get()).asText());
        word.setPosition(root.path(POSITION.get()).asText());

        JsonNode senses = root.path(SENSES.get());
        if (senses.isArray() && senses.isEmpty()) {
            JsonNode firstSense = senses.get(0);
            JsonNode glosses = firstSense.path(GLOSSES.get());
            if (glosses.isArray() && glosses.isEmpty()) {
                word.setDefinition(glosses.get(0).asText());
            }
        }

        List<Inflection> inflections = new ArrayList<>();
        for (JsonNode formNode : root.path(FORMS.get())) {
            Inflection inf = new Inflection();
            inf.setInflection(formNode.path(FORM.get()).asText());

            List<String> tags = new ArrayList<>();
            for (JsonNode tag : formNode.path(TAGS.get())) {
                tags.add(tag.asText());
            }
            inf.setTags(tags);

            inflections.add(inf);
        }

        word.setInflections(inflections);
        return word;
    }

}

