package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.DeclensionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.annepolis.lexiconmeum.web.ApiRoutes.DECLENSION;

@RestController
@RequestMapping("${api.base-path}")
public class LexemeDetailController {

    private final LexemeDetailService lexemeDetailService;

    public LexemeDetailController(LexemeDetailService lexemeDetailService){
        this.lexemeDetailService = lexemeDetailService;
    }

    @GetMapping(DECLENSION)
    public DeclensionTableDTO getDeclensions(@RequestParam String lexemeId){
        return lexemeDetailService.getLexemeDetail(UUID.fromString(lexemeId));
    }
}
