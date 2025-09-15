package com.annepolis.lexiconmeum.webapi.bff.lexemedetail;

import com.annepolis.lexiconmeum.shared.LexemeReader;
import com.annepolis.lexiconmeum.shared.exception.LexemeNotFoundException;
import com.annepolis.lexiconmeum.shared.exception.LexemeTypeMismatchException;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.util.JsonDTOLogger;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailUseCase;
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

    @GetMapping(LEXEME_DETAIL)
    ResponseEntity<LexemeDetailResponse> getLexemeDetail(
            @PathVariable UUID id,
            @RequestParam(name = "type", required = false) GrammaticalPosition expectedType
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
