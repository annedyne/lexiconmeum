package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.DeclensionTableDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lexeme")
public class LexemeDetailController {

    @GetMapping("/detail/declension")
    public DeclensionTableDTO getDeclensions(@RequestParam String lexeme){
        return null;
    }
}
