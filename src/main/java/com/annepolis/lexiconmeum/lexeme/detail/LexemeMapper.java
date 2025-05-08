package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.shared.Lexeme;

public class LexemeMapper {

    public DeclensionTableDTO toDeclensionTableDTO(Lexeme lexeme) {

        DeclensionTableDTO.builder();

       /* Map<String, Map<String, String>> table = new HashMap<>();

        for (Inflection inflection : lexeme.getInflections()) {
            String number = inflection.getNumber();   // e.g. "singular"
            String gramCase = inflection.getCase();   // e.g. "nominative"
            String form = inflection.getForm();       // e.g. "puella"

            table.computeIfAbsent(number, n -> new HashMap<>())
                    .put(gramCase, form);
        }

        return new WordInflectionTableDTO(
                word.getLemma(),
                word.getPartOfSpeech(),
                word.getDeclension(),
                table
        );


    }

        */
    /*
    const nounForms = {
  Nom: { Singular: "lexicon", Plural: "lexica" },
  Gen: { Singular: "lexicis", Plural: "lexicum" },
  Dat: { Singular: "lexico", Plural: "lexicis" },
  Acc: { Singular: "lexicon / -um", Plural: "lexica" },
  Abl: { Singular: "lexic", Plural: "lexicis" }
};

Map<String, String> buildTable(){

    }
    */
        return null;
    }


}
