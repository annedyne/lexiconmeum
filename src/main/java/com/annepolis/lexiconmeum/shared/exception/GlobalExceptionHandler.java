package com.annepolis.lexiconmeum.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorAttributes errorAttributes;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @ExceptionHandler(LexemeTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(LexemeTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleMissingLexeme(NoSuchElementException ex, HttpServletRequest request) {
        Map<String, Object> attrs = buildNotFoundAttributes(request, "Element not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(attrs);
    }

    @ExceptionHandler(LexemeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLexemeNotFoundMap(LexemeNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> attrs = buildNotFoundAttributes(request, "Lexeme not found");
        attrs.put("lexemeId", ex.getLexemeId().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(attrs);
    }

    private Map<String, Object> buildNotFoundAttributes(HttpServletRequest request, String message) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE);
        Map<String, Object> attrs = errorAttributes.getErrorAttributes(new ServletWebRequest(request), options);
        attrs.put("status", HttpStatus.NOT_FOUND.value());
        attrs.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        attrs.put("message", message);
        return attrs;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        logger.error("Illegal state encountered while handling request: {}", ex.getMessage(), ex);

        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE);

        Map<String, Object> attrs = errorAttributes.getErrorAttributes(new ServletWebRequest(request), options);
        attrs.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        attrs.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        attrs.put("message", "Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(attrs);
    }
}
