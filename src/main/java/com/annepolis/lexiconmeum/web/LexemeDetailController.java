package com.annepolis.lexiconmeum.web;

import com.annepolis.lexiconmeum.lexeme.detail.LexemeDetailResponse;
import com.annepolis.lexiconmeum.lexeme.detail.adjective.LexemeAgreementService;
import com.annepolis.lexiconmeum.lexeme.detail.noun.LexemeDeclensionService;
import com.annepolis.lexiconmeum.lexeme.detail.verb.LexemeConjugationService;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import com.annepolis.lexiconmeum.shared.exception.LexemeTypeMismatchException;
import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalPosition;
import com.annepolis.lexiconmeum.shared.util.JsonDTOLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.UUID;

import static com.annepolis.lexiconmeum.web.ApiRoutes.LEXEMES;
import static com.annepolis.lexiconmeum.web.ApiRoutes.LEXEME_DETAIL;

@RestController
@RequestMapping("${api.base-path}")
public class LexemeDetailController {

    static final Logger logger = LogManager.getLogger(LexemeDetailController.class);
    private final LexemeConjugationService lexemeConjugationService;
    private final LexemeDeclensionService lexemeDeclensionService;
    private final LexemeAgreementService lexemeAgreementService;
    private final JsonDTOLogger jsonDTOLogger;
    private final LexemeProvider lexemeProvider;

    public LexemeDetailController(LexemeConjugationService lexemeConjugationService,
                                  LexemeDeclensionService lexemeDeclensionService,
                                  LexemeAgreementService lexemeAgreementService,
                                  LexemeProvider lexemeProvider,
                                  JsonDTOLogger jsonDTOLogger ){
        this.lexemeConjugationService = lexemeConjugationService;
        this.lexemeDeclensionService = lexemeDeclensionService;
        this.lexemeAgreementService = lexemeAgreementService;
        this.lexemeProvider = lexemeProvider;
        this.jsonDTOLogger = jsonDTOLogger;
    }

    @GetMapping(LEXEME_DETAIL)
    public ResponseEntity<LexemeDetailResponse> getLexemeDetail(
            @PathVariable UUID id,
            @RequestParam(name = "type", required = false) GrammaticalPosition expectedType
    ) {

        Lexeme lexeme = lexemeProvider.getLexemeIfPresent(id)
                .orElseThrow(() -> new NoSuchElementException("Lexeme not found"));

        if (expectedType != null && lexeme.getGrammaticalPosition() != expectedType) {
            throw new LexemeTypeMismatchException("Expected " + expectedType + " but got " + lexeme.getGrammaticalPosition());
        }

        LexemeDetailResponse response = switch (lexeme.getGrammaticalPosition()) {
            case NOUN -> lexemeDeclensionService.getLexemeDetail(id);
            case VERB -> lexemeConjugationService.getLexemeDetail(id);
            case ADJECTIVE -> lexemeAgreementService.getLexemeDetail(id);
            default -> throw new UnsupportedOperationException("Detail not implemented for: " + lexeme.getGrammaticalPosition());
        };

        return ResponseEntity.ok(response);
    }

    @GetMapping(LEXEMES)
    public Lexeme getLexeme(@RequestParam String lexemeId){
        logger.debug("fetching lexeme: {}", lexemeId);
        UUID uuid = UUID.fromString(lexemeId);
        Lexeme lexeme = lexemeProvider.getLexemeIfPresent(uuid)
                .orElseThrow(() -> {
                    logger.warn("Lexeme not found: {}", uuid);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Lexeme not found: " + uuid);
                });

        jsonDTOLogger.logAsJson(lexeme);
        return lexeme;
    }

}
