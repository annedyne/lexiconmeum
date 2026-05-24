package com.annepolis.lexiconmeum.webapi.bff.textsearch;

import com.annepolis.lexiconmeum.webapi.bff.textsearch.app.AutocompleteUseCase;
import com.annepolis.lexiconmeum.webapi.bff.textsearch.app.SuggestionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.annepolis.lexiconmeum.webapi.ApiRoutes.PREFIX;
import static com.annepolis.lexiconmeum.webapi.ApiRoutes.SUFFIX;

@RestController
@RequestMapping("${api.base-path}")
@Tag(
        name = "Search Suggestion API",
        description = "Provides autocomplete-style search endpoints that return lexeme suggestions "
                + "based on prefix or suffix matches in inflected forms."
)
public class TextSearchController {

    private final AutocompleteUseCase textSearchService;
    private final TextSearchProperties searchProperties;


    public TextSearchController(AutocompleteUseCase textSearchService, TextSearchProperties searchProperties ){
        this.textSearchService = textSearchService;
        this.searchProperties = searchProperties;
    }

    @Operation(
            summary = "Get lexeme suggestions by prefix",
            description = "Returns lexeme suggestions whose inflected forms begin with the supplied prefix. "
                    + "The number of results is limited by the requested limit and capped by the configured maximum."
    )
    @GetMapping(PREFIX)
    public List<SuggestionResponse> searchByPrefix(@RequestParam String prefix, @RequestParam(required = false) Integer limit) {
        return textSearchService.getWordsStartingWith(prefix, getEffectiveLimit(limit));
    }

    @Operation(
            summary = "Get lexeme suggestions by suffix",
            description = "Returns lexeme suggestions whose inflected forms end with the supplied suffix. "
                    + "The number of results is limited by the requested limit and capped by the configured maximum."
    )
    @GetMapping(SUFFIX)
    public List<SuggestionResponse> searchBySuffix(@RequestParam String suffix, @RequestParam(required = false) Integer limit) {
        return textSearchService.getWordsEndingWith(suffix, getEffectiveLimit(limit));
    }

    private int getEffectiveLimit(Integer clientLimit){
        return (clientLimit == null ? searchProperties.getDefaultLimit() :  Math.min(clientLimit, searchProperties.getResultLimitMax()));
    }


}
