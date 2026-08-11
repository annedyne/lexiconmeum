package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.LexemeBuilder;
import com.annepolis.lexiconmeum.shared.model.grammar.*;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipleTableMapperTest {

    private Lexeme testLexeme = null;

    Lexeme getTestLexeme() {
        if(testLexeme == null){
            testLexeme = createTestLexemeWithParticiples();
        }
        return testLexeme;
    }

    private Lexeme createTestLexemeWithParticiples() {
        VerbDetails.Builder verbDetailsBuilder = new VerbDetails.Builder();

        // Load JSON root
        JsonNode root = loadTestParticiplesJsonRoot();

        List<ParticipleDeclensionSet> participleSets = new ArrayList<>();

        // Build a participle set for each voice/tense
        for (String tenseKey : root.path("participles").propertyNames()) {
            JsonNode tenseNode = root.path("participles").path(tenseKey);

            String voiceStr = tenseNode.path("voice").asString();
            String tenseStr = tenseNode.path("tense").asString();
            String baseForm = tenseNode.path("baseForm").asString();

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

    private JsonNode loadTestParticiplesJsonRoot() {

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("participles.json");
        return mapper.readTree(is);
    }


    private static List<Participle> buildParticipleInflections(JsonNode tenseNode) {
        List<Participle> inflections = new ArrayList<>();

        JsonNode inflectionsNode = tenseNode.path("inflections");
        for (String key : inflectionsNode.propertyNames()) {
            JsonNode entry = inflectionsNode.get(key);
            String form = entry.path("form").asString();

            JsonNode gendersNode = entry.path("genders");
            Participle.Builder builder = new Participle.Builder(form);
            for (JsonNode genderNode : gendersNode) {
                builder.addGender(GrammaticalGender.valueOf(genderNode.asString()));
            }

            String number = entry.path("number").asString();
            builder.setNumber(GrammaticalNumber.valueOf(number));
            String grammaticalCase = entry.path("grammaticalCase").asString();
            builder.setGrammaticalCase(GrammaticalCase.valueOf(grammaticalCase));

            inflections.add(builder.build());
        }
        return inflections;
    }

    @Test
    void participleDTOExistsForAllGenders() {

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
    void eachParticipleDTOHasAllTenses() {
        ParticipleTableMapper underTest = new ParticipleTableMapper();
        List<ParticipleTableDTO> dtos = underTest.toInflectionTableDTO(getTestLexeme());

        // Get the expected tenses (excluding PARTICIPLE which is a base enum value)
        List<GrammaticalParticipleTense> expectedTenses = Arrays.stream(GrammaticalParticipleTense.values())
                .filter(p -> p != GrammaticalParticipleTense.PARTICIPLE
                        && p != GrammaticalParticipleTense.SUPINE
                        && p != GrammaticalParticipleTense.PERFECT_ACTIVE
                        && p != GrammaticalParticipleTense.GERUND)
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

    @Test
    void supineIsMappedInEachGenderedParticipleTable() {
        Participle.Builder accusativeBuilder = new Participle.Builder("amātum")
                .setGrammaticalCase(GrammaticalCase.ACCUSATIVE);
        Participle.Builder ablativeBuilder = new Participle.Builder("amātū")
                .setGrammaticalCase(GrammaticalCase.ABLATIVE);
        for (GrammaticalGender gender : GrammaticalGender.values()) {
            accusativeBuilder.addGender(gender);
            ablativeBuilder.addGender(gender);
        }

        VerbDetails details = ((VerbDetails) getTestLexeme().getPartOfSpeechDetails()).toBuilder()
                .addParticipleSet(ParticipleDeclensionSet.Builder.forVerbalNoun(GrammaticalTense.SUPINE, "amātum")
                        .addInflection(accusativeBuilder.build())
                        .addInflection(ablativeBuilder.build())
                        .build())
                .build();
        Lexeme lexeme = LexemeBuilder.fromLexeme(getTestLexeme())
                .setPartOfSpeechDetails(details)
                .build();

        List<ParticipleTableDTO> tables = new ParticipleTableMapper().toInflectionTableDTO(lexeme);
        for (ParticipleTableDTO table : tables) {
            ParticipleTableDTO.ParticipleTenseDTO supine = table.getTenses().get(table.getTenses().size() - 1);

            assertEquals("Supine", supine.getDefaultName());
            assertEquals(
                    Map.of(
                            GrammaticalNumber.SINGULAR, Map.of(
                                    GrammaticalCase.ACCUSATIVE, "amātum",
                                    GrammaticalCase.ABLATIVE, "amātū"
                            ),
                            GrammaticalNumber.PLURAL, Map.of(
                                    GrammaticalCase.ACCUSATIVE, "amātum",
                                    GrammaticalCase.ABLATIVE, "amātū"
                            )
                    ),
                    supine.getDeclensions()
            );
        }
    }

    @Test
    void gerundIsMappedInEachGenderedParticipleTable() {
        Participle.Builder genitiveBuilder = new Participle.Builder("amandī")
                .setGrammaticalCase(GrammaticalCase.GENITIVE);
        Participle.Builder dativeBuilder = new Participle.Builder("amandō")
                .setGrammaticalCase(GrammaticalCase.DATIVE);
        Participle.Builder accusativeBuilder = new Participle.Builder("amandum")
                .setGrammaticalCase(GrammaticalCase.ACCUSATIVE);
        Participle.Builder ablativeBuilder = new Participle.Builder("amandō")
                .setGrammaticalCase(GrammaticalCase.ABLATIVE);
        for (GrammaticalGender gender : GrammaticalGender.values()) {
            genitiveBuilder.addGender(gender);
            dativeBuilder.addGender(gender);
            accusativeBuilder.addGender(gender);
            ablativeBuilder.addGender(gender);
        }

        VerbDetails details = ((VerbDetails) getTestLexeme().getPartOfSpeechDetails()).toBuilder()
                .addParticipleSet(ParticipleDeclensionSet.Builder
                        .forVerbalNoun(GrammaticalTense.GERUND, "amandum")
                        .addInflection(genitiveBuilder.build())
                        .addInflection(dativeBuilder.build())
                        .addInflection(accusativeBuilder.build())
                        .addInflection(ablativeBuilder.build())
                        .build())
                .build();
        Lexeme lexeme = LexemeBuilder.fromLexeme(getTestLexeme())
                .setPartOfSpeechDetails(details)
                .build();

        List<ParticipleTableDTO> tables = new ParticipleTableMapper().toInflectionTableDTO(lexeme);
        for (ParticipleTableDTO table : tables) {
            ParticipleTableDTO.ParticipleTenseDTO gerund = table.getTenses().get(table.getTenses().size() - 1);

            assertEquals("Gerund", gerund.getDefaultName());
            assertEquals(
                    Map.of(
                            GrammaticalNumber.SINGULAR, Map.of(
                                    GrammaticalCase.GENITIVE, "amandī",
                                    GrammaticalCase.DATIVE, "amandō",
                                    GrammaticalCase.ACCUSATIVE, "amandum",
                                    GrammaticalCase.ABLATIVE, "amandō"
                            ),
                            GrammaticalNumber.PLURAL, Map.of(
                                    GrammaticalCase.GENITIVE, "amandī",
                                    GrammaticalCase.DATIVE, "amandō",
                                    GrammaticalCase.ACCUSATIVE, "amandum",
                                    GrammaticalCase.ABLATIVE, "amandō"
                            )
                    ),
                    gerund.getDeclensions()
            );
        }
    }
}
