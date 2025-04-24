package com.annepolis.lexiconmeum.data;

import com.annepolis.lexiconmeum.domain.model.Inflection;
import com.annepolis.lexiconmeum.domain.model.Word;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.annepolis.lexiconmeum.util.JsonKey.*;

@Component
public class WiktionaryParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Word> parseJsonl(Reader reader) throws IOException {
        List<Word> words = new ArrayList<>();

        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            JsonNode root = mapper.readTree(line);
            Word word = parseWord(root);
            words.add(word);
        }

        return words;
    }

    private Word parseWord(JsonNode root) {
        Word word = new Word();
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

