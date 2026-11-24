package de.nrw.it.ignrw.handler;

import tools.jackson.databind.exc.InvalidFormatException;
import de.nrw.it.ignrw.exception.DsgvoException;
import tools.jackson.databind.exc.ValueInstantiationException;
import de.nrw.it.ignrw.exception.ResourceNotFoundException;
import de.nrw.it.ignrw.exception.response.ErrorCode;
import de.nrw.it.ignrw.exception.response.ErrorResponse;
import de.nrw.it.ignrw.exception.response.ErrorResponseFactory;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String getErrorMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    // ==========================================================
    // BODY validation
    // ==========================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(MethodArgumentNotValidException ex) {

        log.warn("Validation error in request body", ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.bodyValidation"),
                ErrorCode.BODY_VALIDATION_ERROR,
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }


    // ==========================================================
    // PARAM validation
    // ==========================================================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        log.warn("Parameter validation failed", ex);

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.paramValidation"),
                ErrorCode.PARAM_VALIDATION_ERROR,
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }


    // ==========================================================
    // Parameter Type Mismatch
    // ==========================================================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        log.warn("Type mismatch for parameter {}", ex.getName(), ex);

        Map<String, String> errors = Map.of(
                ex.getName(),
                "Invalid value. Expected type: " + (
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
                )
        );

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.typeMismatch"),
                ErrorCode.TYPE_MISMATCH_ERROR,
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    // ==========================================================
    // JSON Deserialization Error (z.B. ungültige UUID im Body)
    // ==========================================================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonMappingErrors(HttpMessageNotReadableException ex) {

        log.warn("Failed to read HTTP message / JSON deserialization failed", ex);

        Map<String, String> errors = new HashMap<>();

        if (ex.getCause() instanceof InvalidFormatException ife) {

            String fieldName = ife.getPath().isEmpty() ? "unknown" : ife.getPath().getFirst().getFieldName();

            if (ife.getTargetType().equals(java.util.UUID.class)) {
                errors.put(fieldName, "Invalid UUID format. Provided value: '" + ife.getValue() + "'");
            } else {
                errors.put(fieldName, "Invalid value for expected type: " + ife.getTargetType().getSimpleName());
            }
        }  else if (ex.getCause() instanceof ValueInstantiationException vie) {
            String fieldName = vie.getPath().isEmpty() ? "unknown" : vie.getPath().getFirst().getPropertyName();
            errors.put(fieldName, vie.getCause().getMessage());
        } else {
            errors.put("body", "Malformed JSON request body");
        }

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.bodyValidation"),
                ErrorCode.BODY_VALIDATION_ERROR,
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    // ==========================================================
    // ResourceNotFoundException → 404
    // ==========================================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

        log.info("Resource not found: {}", ex.getMessage());

        String message;

        if (ex.getMessage() != null){
            message = ex.getMessage();
        } else {
            message = getErrorMessage("error.notFound");
        }

        ErrorResponse response = ErrorResponseFactory.build(
                message,
                ErrorCode.NOT_FOUND,
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    // ==========================================================
    // AccessDeniedException → 403
    // ==========================================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {

        log.warn("Access denied");

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.accessDenied"),
                ErrorCode.ACCESS_DENIED,
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DsgvoException.class)
    public ResponseEntity<ErrorResponse> handleDsgvoAccessDenied(DsgvoException ex) {

        log.warn("Dsgvo not accepted");

        Map<String, String> errors = Map.of(
                "dsgvo",
                "not accepted"
        );

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.accessDenied"),
                ErrorCode.ACCESS_DENIED,
                errors
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


    // ==========================================================
    // AuthenticationException → 401
    // ==========================================================
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex) {

        log.warn("Authentication required");

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.authRequired"),
                ErrorCode.AUTH_REQUIRED,
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    // ==========================================================
    // Unexpected Runtime Exceptions → 500 INTERNAL SERVER ERROR
    // ==========================================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(RuntimeException ex) {

        log.error("Unexpected server error", ex);

        ErrorResponse response = ErrorResponseFactory.build(
                getErrorMessage("error.unexpected"),
                ErrorCode.UNEXPECTED_ERROR,
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
