package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.textsearch.TextSearchProperties;
import com.annepolis.lexiconmeum.textsearch.TextSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.annepolis.lexiconmeum.web.ApiRoutes.PREFIX;
import static com.annepolis.lexiconmeum.web.ApiRoutes.SUFFIX;

@RestController
@RequestMapping("${api.base-path}")
public class TextSearchController {

    private final TextSearchService textSearchService;
    private final TextSearchProperties searchProperties;


    public TextSearchController(TextSearchService textSearchService, TextSearchProperties searchProperties ){
        this.textSearchService = textSearchService;
        this.searchProperties = searchProperties;
    }

    @GetMapping(PREFIX)
    public List<String> searchByPrefix(@RequestParam String prefix, @RequestParam(required = false) Integer limit) {
        return textSearchService.getWordsStartingWith(prefix, getEffectiveLimit(limit));
    }

    @GetMapping(SUFFIX)
    public List<String> searchBySuffix(@RequestParam String suffix, @RequestParam(required = false) Integer limit) {
        return textSearchService.getWordsEndingWith(suffix, getEffectiveLimit(limit));
    }

    private int getEffectiveLimit(Integer limit){
        return (limit == null || limit > searchProperties.getDefaultLimit())
                ? searchProperties.getDefaultLimit()
                : limit;
    }


}
