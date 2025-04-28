package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.textsearch.TextSearchComponent;
import com.annepolis.lexiconmeum.textsearch.TextSearchProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final TextSearchComponent textSearchComponent;
    private final TextSearchProperties searchProperties;

    public SearchController(TextSearchComponent textSearchComponent, TextSearchProperties searchProperties ){
        this.textSearchComponent = textSearchComponent;
        this.searchProperties = searchProperties;
    }

    @GetMapping("/prefix")
    public List<String> searchByPrefix(@RequestParam String prefix, @RequestParam(required = false) Integer limit) {

        return textSearchComponent.getWordsStartingWith(prefix, getEffectiveLimit(limit));
    }

    @GetMapping("/suffix")
    public List<String> searchBySuffix(@RequestParam String suffix, @RequestParam(required = false) Integer limit) {
        return textSearchComponent.getWordsEndingWith(suffix, getEffectiveLimit(limit));
    }

    private int getEffectiveLimit(Integer limit){
        return (limit == null || limit > searchProperties.getDefaultLimit())
                ? searchProperties.getDefaultLimit()
                : limit;
    }


}
