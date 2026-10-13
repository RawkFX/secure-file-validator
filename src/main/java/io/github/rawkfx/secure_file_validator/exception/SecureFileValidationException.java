package io.github.rawkfx.secure_file_validator.exception;

/**
 * Exception thrown when a file fails validation.
 */
public class SecureFileValidationException extends RuntimeException {
    public SecureFileValidationException(String message) {
        super(message);
    }
}