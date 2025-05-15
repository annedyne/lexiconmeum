package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.DeclensionTableDTO;
import com.annepolis.lexiconmeum.lexeme.detail.LexemeDeclensionDetailComponent;
import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lexeme")
public class LexemeDetailController {

    private LexemeDeclensionDetailComponent declensionDetailProvider;
    private LexemeProvider lexemeProvider;

    public LexemeDetailController(LexemeDeclensionDetailComponent declensionDetailProvider, LexemeProvider lexemeProvider){
        this.declensionDetailProvider = declensionDetailProvider;
        this.lexemeProvider = lexemeProvider;
    }

    @GetMapping("/detail/declension")
    public DeclensionTableDTO getDeclensions(@RequestParam String lemma){
        Lexeme lexeme = lexemeProvider.getLexeme(lemma);
        return declensionDetailProvider.getLexemeDetail(lexeme);
    }
}
