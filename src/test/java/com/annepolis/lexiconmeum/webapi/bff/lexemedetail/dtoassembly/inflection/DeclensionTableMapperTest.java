package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection;

import com.annepolis.lexiconmeum.ingest.wiktionary.JsonTestDataManager;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DeclensionTableMapperTest {

    @Test
    void toDeclensionTableDTOCorrectlyFormed() throws IOException {
        DeclensionTableMapper mapper = new DeclensionTableMapper();
        Lexeme nounLexeme = JsonTestDataManager.INSTANCE.getParsedNounLexeme("poculum", "testDataNoun.jsonl");
        DeclensionTableDTO tableDTO = mapper.toInflectionTableDTO(nounLexeme);
        Assertions.assertEquals("pōculum", tableDTO.getInflectionTable().get(GrammaticalNumber.SINGULAR).get(GrammaticalCase.NOMINATIVE));
    }


}
