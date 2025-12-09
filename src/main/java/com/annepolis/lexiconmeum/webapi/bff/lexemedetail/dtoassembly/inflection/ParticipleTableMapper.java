package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.ParticipleDeclensionSet;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.VerbDetails;
import com.annepolis.lexiconmeum.shared.model.inflection.Inflection;
import com.annepolis.lexiconmeum.shared.model.inflection.Participle;
import org.springframework.stereotype.Component;

import java.util.*;

// Group by gender
// Sort by tense
// create a declension table for each tense
@Component
public class ParticipleTableMapper {

    public List<ParticipleTableDTO> toInflectionTableDTO(Lexeme lexeme) {
        // From the list of participle inflections grouped by tense
        // We are going to re-map them by gender, then tense
        Map<GrammaticalGender, List<ParticipleTableDTO.ParticipleTenseDTO>> byGender = getTenseDTOsByGender(extractParticiples(lexeme));

        // Now we wrap the list of tenseDTOs for a given gender into a ParticipleTableDTO
       List<ParticipleTableDTO> participles = new ArrayList<>();

        for (Map.Entry<GrammaticalGender,List<ParticipleTableDTO.ParticipleTenseDTO>> entry : byGender.entrySet()) {
            String gender = entry.getKey().getTag();
            ParticipleTableDTO dto = new ParticipleTableDTO();
            dto.setGender(gender);
            dto.setTenses(entry.getValue());
            participles.add(dto);
        }
        return participles;
    }

    Map<GrammaticalGender, List<ParticipleTableDTO.ParticipleTenseDTO>> getTenseDTOsByGender(Map<String, ParticipleDeclensionSet> participleSetMap){
        Map<GrammaticalGender, List<ParticipleTableDTO.ParticipleTenseDTO>> byGender = new LinkedHashMap<>();

        // For each participle set (all inflections for all genders for a given tense )
        for (ParticipleDeclensionSet participleSet : participleSetMap.values()) {

            // Extract all inflections for the given set (all inflections for all genders for current tense)
            Map<String, Inflection> inflections = participleSet.getInflectionIndex();

            // Group the inflections for the current tense by gender
            Map<GrammaticalGender, List<Participle>> inflectionsByGender = groupInflectionsByGender(inflections.values());

            // for a given gender (for this tense) create a TenseDTO
            for (Map.Entry<GrammaticalGender, List<Participle>> entry : inflectionsByGender.entrySet()) {
                GrammaticalGender gender = entry.getKey();
                ParticipleTableDTO.ParticipleTenseDTO tenseDTO = getTenseDTOWithGender(participleSet, entry.getValue());

                // Add the tenseDTO to the map with its associated gender key
                byGender.computeIfAbsent(gender, k -> new ArrayList<>()).add(tenseDTO);
            }
        }
        return byGender;
    }

    // Create a new
    private ParticipleTableDTO.ParticipleTenseDTO getTenseDTOWithGender(ParticipleDeclensionSet participleSet, List<Participle> participles) {
        // Create a declension table for this gender/tense combination
        DeclensionTableDTO declensionTable = createDeclensionTable(participles);

        // Create tense DTO for the given gen
        ParticipleTableDTO.ParticipleTenseDTO tenseDTO = new ParticipleTableDTO.ParticipleTenseDTO();
        tenseDTO.setDefaultName(participleSet.getParticipleTense().getDisplayName());
        tenseDTO.setAltName(participleSet.getParticipleTense().getAlternativeName());
        tenseDTO.setDeclensions(declensionTable);
        return tenseDTO;
    }

    private DeclensionTableDTO createDeclensionTable(List<Participle> participles) {
        DeclensionTableDTO declensionTable = new DeclensionTableDTO();
        Map<GrammaticalNumber, Map<GrammaticalCase, String>> table = new EnumMap<>(GrammaticalNumber.class);

        for (Participle participle : participles) {
            GrammaticalNumber number = participle.getNumber();
            GrammaticalCase grammaticalCase = participle.getGrammaticalCase();
            String form = participle.getForm();

            if (number != null && grammaticalCase != null) {
                table.computeIfAbsent(number, k -> new EnumMap<>(GrammaticalCase.class))
                        .put(grammaticalCase, form);
            }
        }

        declensionTable.setInflectionTable(table);
        return declensionTable;
    }

    private Map<GrammaticalGender, List<Participle>> groupInflectionsByGender(Collection<Inflection> inflections) {
        Map<GrammaticalGender, List<Participle>> byGender = new EnumMap<>(GrammaticalGender.class);

        for (Inflection inflection : inflections) {
            if(inflection instanceof Participle participle ) {
                Set<GrammaticalGender> genders = participle.getGenders();


                // Handle multiple genders - if an inflection has multiple genders,
                // it applies to all of them
                for (GrammaticalGender gender : genders) {
                    byGender.computeIfAbsent(gender, k -> new ArrayList<>()).add(participle);
                }
            }
        }

        return byGender;
    }

    Map<String, ParticipleDeclensionSet> extractParticiples(Lexeme lexeme){
        if (lexeme.getPartOfSpeechDetails() instanceof VerbDetails details) {
           return  details.getParticiples();
        }

        throw new IllegalStateException(
                "Expected VerbDetails for lexeme " + lexeme.getLemma() + " but found: "
                        + (lexeme.getPartOfSpeechDetails() == null ? "null"
                        : lexeme.getPartOfSpeechDetails().getClass().getSimpleName()));
    }
}
