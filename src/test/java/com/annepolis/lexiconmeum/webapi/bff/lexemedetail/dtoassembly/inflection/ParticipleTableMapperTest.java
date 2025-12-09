package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipleTableMapperTest {

    private Lexeme testLexeme = null;

    Lexeme getTestLexeme() throws IOException {
        if(testLexeme == null){
            testLexeme = createTestLexemeWithParticiples();
        }
        return testLexeme;
    }

    private Lexeme createTestLexemeWithParticiples() throws IOException {
        VerbDetails.Builder verbDetailsBuilder = new VerbDetails.Builder();

        // Load JSON root
        JsonNode root = loadTestParticiplesJsonRoot();

        List<ParticipleDeclensionSet> participleSets = new ArrayList<>();

        // Build a participle set for each voice/tense
        for (Iterator<String> it = root.path("participles").fieldNames(); it.hasNext(); ) {
            String tenseKey = it.next();
            JsonNode tenseNode = root.path("participles").path(tenseKey);

            String voiceStr = tenseNode.path("voice").asText();
            String tenseStr = tenseNode.path("tense").asText();
            String baseForm = tenseNode.path("baseForm").asText();

            // build the declensions for this participle set
            List<Participle> inflections = buildParticipleInflections(tenseNode);

            // Create the DeclensionSet builder
            ParticipleDeclensionSet.Builder participleSetBuilder = new ParticipleDeclensionSet.Builder(
                    GrammaticalVoice.valueOf(voiceStr),
                    GrammaticalTense.valueOf(tenseStr),
                    baseForm
            );

            // Add the inflections
            participleSetBuilder.addInflections(inflections);

            // Collect the declension sets
            participleSets.add(participleSetBuilder.build());
        }

        // Add them to VerbDetails
        for(ParticipleDeclensionSet participleSet : participleSets){
            verbDetailsBuilder.addParticipleSet(participleSet);
        }

        LexemeBuilder lexemeBuilder = new LexemeBuilder("amo", PartOfSpeech.VERB, "1");
        return lexemeBuilder.setPartOfSpeechDetails(verbDetailsBuilder.build()).build();
    }

    private JsonNode loadTestParticiplesJsonRoot() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("participles.json");
        return mapper.readTree(is);
    }


    private static List<Participle> buildParticipleInflections(JsonNode tenseNode) {
        List<Participle> inflections = new ArrayList<>();

        JsonNode inflectionsNode = tenseNode.path("inflections");
        Iterator<String> keys = inflectionsNode.fieldNames();

        while (keys.hasNext()) {
            String key = keys.next();
            JsonNode entry = inflectionsNode.get(key);
            String form = entry.path("form").asText();

            JsonNode gendersNode = entry.path("genders");
            Participle.Builder builder = new Participle.Builder(form);
            for (JsonNode genderNode : gendersNode) {
                builder.addGender(GrammaticalGender.valueOf(genderNode.asText()));
            }

            String number = entry.path("number").asText();
            builder.setNumber(GrammaticalNumber.valueOf(number));
            String grammaticalCase = entry.path("grammaticalCase").asText();
            builder.setGrammaticalCase(GrammaticalCase.valueOf(grammaticalCase));

            inflections.add(builder.build());
        }
        return inflections;
    }

    @Test
    void participleDTOExistsForAllGenders() throws Exception {

        ParticipleTableMapper underTest = new ParticipleTableMapper();
        List<ParticipleTableDTO> dtos = underTest.toInflectionTableDTO(getTestLexeme());

        assertEquals(GrammaticalGender.values().length, dtos.size());

        for (GrammaticalGender gender : GrammaticalGender.values()){
             dtos.stream().filter(dto -> dto.getGender().equals(gender.getTag()))
                     .findFirst().orElseThrow(() ->
                             new AssertionError("No participles found for gender: " + gender.getTag()));
        }
    }

    @Test
    void eachParticipleDTOHasAllTenses() throws Exception {
        ParticipleTableMapper underTest = new ParticipleTableMapper();
        List<ParticipleTableDTO> dtos = underTest.toInflectionTableDTO(getTestLexeme());

        // Get the expected tenses (excluding PARTICIPLE which is a base enum value)
        List<GrammaticalParticipleTense> expectedTenses = Arrays.stream(GrammaticalParticipleTense.values())
                .filter(p -> p != GrammaticalParticipleTense.PARTICIPLE)
                .toList();

        for (ParticipleTableDTO dto : dtos) {
            List<ParticipleTableDTO.ParticipleTenseDTO> tenses = dto.getTenses();

            assertEquals(expectedTenses.size(), tenses.size(),
                    "Gender " + dto.getGender() + " should have all tenses");

            // Verify each expected tense is present
            for (GrammaticalParticipleTense expectedTense : expectedTenses) {
                boolean found = tenses.stream()
                        .anyMatch(t -> t.getDefaultName().equals(expectedTense.getDisplayName()));

                if (!found) {
                    throw new AssertionError("Gender " + dto.getGender() +
                            " is missing tense: " + expectedTense.getDisplayName());
                }
            }
        }
    }
}
