package com.annepolis.lexiconmeum.controller;

import com.annepolis.lexiconmeum.search.Lexicon;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final Lexicon lexicon;

    public SearchController(Lexicon lexicon){
        this.lexicon = lexicon;
    }

    @GetMapping("/prefix")
    public List<String> searchByPrefix(@RequestParam String prefix, @RequestParam(defaultValue = "10") int limit) {
        return lexicon.getWordsStartingWith(prefix, limit);
    }

    @GetMapping("/suffix")
    public List<String> searchBySuffix(@RequestParam String suffix, @RequestParam(defaultValue = "10") int limit) {
        return lexicon.getWordsEndingWith(suffix, limit);
    }
}
