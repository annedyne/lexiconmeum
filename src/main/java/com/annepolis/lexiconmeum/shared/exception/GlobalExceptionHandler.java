package com.annepolis.lexiconmeum.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LexemeTypeMismatchException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleTypeMismatch(LexemeTypeMismatchException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(LexemeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLexemeNotFound(LexemeNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Lexeme not found", request, ex.getLexemeId().toString());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        logger.error("Illegal state encountered while handling request: {}", ex.getMessage(), ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, null);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request, String lexemeId) {
        return new ErrorResponse(status.value(),status.getReasonPhrase(),message, request.getRequestURI(), lexemeId );
    }
}
