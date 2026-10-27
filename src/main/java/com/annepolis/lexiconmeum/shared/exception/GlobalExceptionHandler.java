package com.annepolis.lexiconmeum.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;

import static com.annepolis.lexiconmeum.webapi.ApiResponseKeys.LEXEME_ID;

/**
 * Maps exceptions to RFC 9457 Problem Details responses.
 * <p>
 * Spring MVC's own exceptions (unmapped paths, unsupported methods, media types,
 * missing/invalid parameters, ...) are handled by {@link ResponseEntityExceptionHandler}
 * with their intended 4xx status. The handlers below cover domain exceptions, and the
 * {@link Exception} catch-all is reserved for genuinely unexpected failures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    private static final String INTERNAL_ERROR_DETAIL = "Internal server error";

    @ExceptionHandler(LexemeTypeMismatchException.class)
    public ProblemDetail handleLexemeTypeMismatch(LexemeTypeMismatchException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), ex.getLexemeId());
    }

    @ExceptionHandler(LexemeNotFoundException.class)
    public ProblemDetail handleLexemeNotFound(LexemeNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Lexeme not found", ex.getLexemeId());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .orElse("Request validation failed.");

        return problem(HttpStatus.BAD_REQUEST, detail, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        logger.error("Illegal state encountered while handling request: {}", ex.getMessage(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_DETAIL, null);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        logger.error("Unexpected error while handling request: {}", ex.getMessage(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_DETAIL, null);
    }

    /**
     * Overrides Spring's default detail, which echoes the rejected value back to the caller.
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Class<?> requiredType = ex.getRequiredType() != null ? ex.getRequiredType() : Object.class;
        String detail = "Invalid value for parameter '%s'. Expected %s."
                .formatted(ex.getPropertyName(), requiredType.getSimpleName());

        return handleExceptionInternal(ex, ProblemDetail.forStatusAndDetail(status, detail), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String detail = "Missing required parameter '%s'.".formatted(ex.getParameterName());

        return handleExceptionInternal(ex, ProblemDetail.forStatusAndDetail(status, detail), headers, status, request);
    }

    private ProblemDetail problem(HttpStatus status, String detail, UUID lexemeId) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        if (lexemeId != null) {
            problem.setProperty(LEXEME_ID, lexemeId.toString());
        }
        return problem;
    }
}
