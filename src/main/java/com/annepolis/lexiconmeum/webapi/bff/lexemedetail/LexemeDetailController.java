package com.annepolis.lexiconmeum.webapi.bff.lexemedetail;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.shared.exception.LexemeNotFoundException;
import com.annepolis.lexiconmeum.shared.exception.LexemeTypeMismatchException;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.util.JsonDTOLogger;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.annepolis.lexiconmeum.webapi.ApiRoutes.LEXEMES;
import static com.annepolis.lexiconmeum.webapi.ApiRoutes.LEXEME_DETAIL;

@RestController
@RequestMapping("${api.base-path}")
class LexemeDetailController {

    static final Logger logger = LogManager.getLogger(LexemeDetailController.class);
    private final LexemeDetailUseCase lexemeDetailUseCase;
    private final JsonDTOLogger jsonDTOLogger;
    private final LexemeReader lexemeReader;

    LexemeDetailController(LexemeDetailUseCase lexemeDetailUseCase,
                           LexemeReader lexemeReader,
                           JsonDTOLogger jsonDTOLogger ){
        this.lexemeDetailUseCase = lexemeDetailUseCase;
        this.lexemeReader = lexemeReader;
        this.jsonDTOLogger = jsonDTOLogger;
    }

    @Operation(
        summary = "Get curated lexeme details by ID",
        description = "Returns lexical and grammatical information for a lexeme in a curated, presentation-ready structure designed for client consumption."
)
    @GetMapping(LEXEME_DETAIL)
    ResponseEntity<LexemeDetailResponse> getLexemeDetail(
        @PathVariable UUID id,
        @RequestParam(name = "type", required = false) PartOfSpeech expectedType
) {
    Lexeme lexeme = getLexeme(id);

    if (expectedType != null && lexeme.getPartOfSpeech() != expectedType) {
        throw new LexemeTypeMismatchException("Expected " + expectedType + " but got " + lexeme.getPartOfSpeech());
    }
    LexemeDetailResponse response = lexemeDetailUseCase.execute(lexeme);
    jsonDTOLogger.logAsJson(response);
    return ResponseEntity.ok(response);
}

    @GetMapping(LEXEMES)
    @Operation(
        summary = "Get raw lexeme model by ID",
        description = "Returns the raw lexeme domain model as JSON. This endpoint is primarily intended for inspection, debugging, and comparison with the curated lexeme detail response, rather than for direct presentation in clients."
)
    Lexeme getLexeme(@RequestParam String lexemeId){
        logger.debug("fetching lexeme: {}", lexemeId);
        UUID uuid = UUID.fromString(lexemeId);
        Lexeme lexeme = getLexeme(uuid);

        jsonDTOLogger.logAsJson(lexeme);
        return lexeme;
    }

    private Lexeme getLexeme(UUID lexemeId ){
        return lexemeReader.getLexemeIfPresent(lexemeId)
                .orElseThrow(() -> {
                    logger.warn("Lexeme not found: {}", lexemeId);
                    return new LexemeNotFoundException(lexemeId);
                });
    }

}
