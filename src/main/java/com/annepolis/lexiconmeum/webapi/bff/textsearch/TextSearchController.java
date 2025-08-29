package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.annepolis.lexiconmeum.webapi.ApiRoutes.PREFIX;
import static com.annepolis.lexiconmeum.webapi.ApiRoutes.SUFFIX;

@RestController
@RequestMapping("${api.base-path}")
public class TextSearchController {

    private final TextSearchSuggestionService textSearchService;
    private final TextSearchProperties searchProperties;


    public TextSearchController(TextSearchSuggestionService textSearchService, TextSearchProperties searchProperties ){
        this.textSearchService = textSearchService;
        this.searchProperties = searchProperties;
    }

    @GetMapping(PREFIX)
    public List<TextSearchSuggestionDTO> searchByPrefix(@RequestParam String prefix, @RequestParam(required = false) Integer limit) {
        return textSearchService.getWordsStartingWith(prefix, getEffectiveLimit(limit));
    }

    @GetMapping(SUFFIX)
    public List<TextSearchSuggestionDTO> searchBySuffix(@RequestParam String suffix, @RequestParam(required = false) Integer limit) {
        return textSearchService.getWordsEndingWith(suffix, getEffectiveLimit(limit));
    }

    private int getEffectiveLimit(Integer clientLimit){
        return (clientLimit == null ? searchProperties.getDefaultLimit() :  Math.min(clientLimit, searchProperties.getResultLimitMax()));
    }


}
