package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.noun.DeclensionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.noun.LexemeDeclensionService;
import com.annepolis.lexiconmeum.lexeme.detail.verb.ConjugationTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.verb.LexemeConjugationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.annepolis.lexiconmeum.web.ApiRoutes.CONJUGATION;
import static com.annepolis.lexiconmeum.web.ApiRoutes.DECLENSION;

@RestController
@RequestMapping("${api.base-path}")
public class LexemeDetailController {
    static final Logger LOGGER = LogManager.getLogger(LexemeDetailController.class);
    private final LexemeConjugationService lexemeConjugationService;
    private final LexemeDeclensionService lexemeDeclensionService;

    public LexemeDetailController(LexemeConjugationService lexemeConjugationService,
                                  LexemeDeclensionService lexemeDeclensionService){
        this.lexemeConjugationService = lexemeConjugationService;
        this.lexemeDeclensionService = lexemeDeclensionService;
    }

    @GetMapping(DECLENSION)
    public DeclensionTableDTO getDeclensions(@RequestParam String lexemeId){
        LOGGER.debug("fetching lexeme: {}", lexemeId);
        DeclensionTableDTO table = lexemeDeclensionService.getLexemeDetail(UUID.fromString(lexemeId));
        LOGGER.info("returning lexeme: {}", lexemeId);
        return table;
    }


    @GetMapping(CONJUGATION)
    public ConjugationTableDTO getConjugations(@RequestParam String lexemeId){
        LOGGER.debug("fetching lexeme: {}", lexemeId);
        ConjugationTableDTO table = lexemeConjugationService.getLexemeDetail(UUID.fromString(lexemeId));
        LOGGER.info("returning lexeme: {}", lexemeId);
        return table;
    }
}
