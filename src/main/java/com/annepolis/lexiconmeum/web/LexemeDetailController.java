package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.DeclensionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.annepolis.lexiconmeum.web.ApiRoutes.DECLENSION;

@RestController
@RequestMapping("${api.base-path}")
public class LexemeDetailController {
    static final Logger LOGGER = LogManager.getLogger(LexemeDetailController.class);
    private final LexemeDetailService lexemeDetailService;

    public LexemeDetailController(LexemeDetailService lexemeDetailService){
        this.lexemeDetailService = lexemeDetailService;
    }

    @GetMapping(DECLENSION)
    public DeclensionTableDTO getDeclensions(@RequestParam String lexemeId){
        LOGGER.debug("fetching lexeme: {}", lexemeId);
        DeclensionTableDTO table = lexemeDetailService.getLexemeDetail(UUID.fromString(lexemeId));
        LOGGER.info("returning lexeme: {}", lexemeId);
        return table;
    }
}
