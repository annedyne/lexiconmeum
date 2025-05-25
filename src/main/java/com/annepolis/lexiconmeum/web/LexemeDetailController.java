package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.DeclensionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDeclensionDetailComponent;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.annepolis.lexiconmeum.web.ApiRoutes.DECLENSION;

@RestController
@RequestMapping("${api.base-path}")
public class LexemeDetailController {

    private LexemeDeclensionDetailComponent declensionDetailProvider;
    private LexemeProvider lexemeProvider;

    public LexemeDetailController(LexemeDeclensionDetailComponent declensionDetailProvider, LexemeProvider lexemeProvider){
        this.declensionDetailProvider = declensionDetailProvider;
        this.lexemeProvider = lexemeProvider;
    }

    @GetMapping(DECLENSION)
    public DeclensionTableDTO getDeclensions(@RequestParam String lexemeId){
        Lexeme lexeme = lexemeProvider.getLexeme(UUID.fromString(lexemeId));
        return declensionDetailProvider.getLexemeDetail(lexeme);
    }
}
