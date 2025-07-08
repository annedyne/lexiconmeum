package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.noun.DeclensionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.noun.LexemeDeclensionService;
import com.annepolis.lexiconmeum.lexeme.detail.verb.ConjugationGroupDTO;
import com.annepolis.lexiconmeum.lexeme.detail.verb.LexemeConjugationService;
import com.annepolis.lexiconmeum.shared.util.JsonDTOLogger;
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

    static final Logger logger = LogManager.getLogger(LexemeDetailController.class);
    private final LexemeConjugationService lexemeConjugationService;
    private final LexemeDeclensionService lexemeDeclensionService;
    private final JsonDTOLogger jsonDTOLogger;

    public LexemeDetailController(LexemeConjugationService lexemeConjugationService,
                                  LexemeDeclensionService lexemeDeclensionService, JsonDTOLogger jsonDTOLogger){
        this.lexemeConjugationService = lexemeConjugationService;
        this.lexemeDeclensionService = lexemeDeclensionService;
        this.jsonDTOLogger = jsonDTOLogger;
    }

    @GetMapping(DECLENSION)
    public DeclensionTableDTO getDeclensions(@RequestParam String lexemeId){
        logger.debug("fetching lexeme: {}", lexemeId);
        DeclensionTableDTO table = lexemeDeclensionService.getLexemeDetail(UUID.fromString(lexemeId));
        jsonDTOLogger.logAsJson(table);
        return table;
    }


    @GetMapping(CONJUGATION)
    public ConjugationGroupDTO getConjugations(@RequestParam String lexemeId){
        logger.debug("fetching lexeme: {}", lexemeId);
        ConjugationGroupDTO tables = lexemeConjugationService.getLexemeDetail(UUID.fromString(lexemeId));
        jsonDTOLogger.logAsJson(tables);
        return tables;
    }

}
