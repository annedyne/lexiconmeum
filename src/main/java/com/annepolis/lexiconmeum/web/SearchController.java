package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.textsearch.TextSearchComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final TextSearchComponent textSearchComponent;

    public SearchController(TextSearchComponent textSearchComponent){
        this.textSearchComponent = textSearchComponent;
    }

    @GetMapping("/prefix")
    public List<String> searchByPrefix(@RequestParam String prefix, @RequestParam(defaultValue = "10") int limit) {
        return textSearchComponent.getWordsStartingWith(prefix, limit);
    }

    @GetMapping("/suffix")
    public List<String> searchBySuffix(@RequestParam String suffix, @RequestParam(defaultValue = "10") int limit) {
        return textSearchComponent.getWordsEndingWith(suffix, limit);
    }
}
