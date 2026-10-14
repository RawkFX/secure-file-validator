package io.github.rawkfx.secure_file_validator.handler;

import io.github.rawkfx.secure_file_validator.exception.SecureFileValidationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecureFileExceptionHandler {
    /**
     * Hibernate Validator wraps RuntimeExceptions thrown in ConstraintValidators
     * inside a generic ValidationException. We unwrap it here.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException exception, HttpServletRequest request) {
        if (exception.getCause() instanceof SecureFileValidationException) {
            SecureFileValidationException secureFileException = (SecureFileValidationException) exception.getCause();

            return this.buildErrorResponse(secureFileException.getMessage(), request.getRequestURI());
        }

        // If it's a different validation exception, re-throw it so the consuming app's
        throw exception;
    }

    /**
     * Fallback in case it's thrown outside of the standard Bean Validation lifecycle.
     */
    @ExceptionHandler(SecureFileValidationException.class)
    public ResponseEntity<Object> handleSecureFileException(SecureFileValidationException exception, HttpServletRequest request) {
        return this.buildErrorResponse(exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<Object> buildErrorResponse(String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", message);
        body.put("path", path);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}