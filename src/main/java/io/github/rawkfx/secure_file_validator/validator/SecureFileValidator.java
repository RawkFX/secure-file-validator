package io.github.rawkfx.secure_file_validator.validator;

import io.github.rawkfx.secure_file_validator.annotation.SecureFile;
import io.github.rawkfx.secure_file_validator.exception.SecureFileValidationException;
import io.github.rawkfx.secure_file_validator.properties.SecureFileValidatorProperties;
import org.apache.tika.Tika;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;

/**
 * Validator for the @SecureFile annotation. Validates the uploaded file based on size, MIME type, and extension.
 * It uses Apache Tika to detect the MIME type of the file.
 * The validation messages can be customized through the annotation or application properties.
 * <p>
 * Failures are handled by throwing SecureFileValidationException to short-circuit processing.
 */
public class SecureFileValidator implements ConstraintValidator<SecureFile, MultipartFile> {
    private final Tika tika;
    private final SecureFileValidatorProperties properties;
    private SecureFile annotation;

    // Constructor for SecureFileValidator.
    public SecureFileValidator(Tika tika, SecureFileValidatorProperties properties) {
        this.tika = tika;
        this.properties = properties;
    }

    @Override
    public void initialize(SecureFile annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        boolean isNull = (file == null);
        boolean isEmpty = (!isNull && file.isEmpty());

        if (isNull || isEmpty) {
            this.handleMissingFile(isNull);

            // If it doesn't throw an exception, it means required() is false
            return true;
        }

        this.validateSafeFilename(file);
        this.validateSize(file);
        this.validateMimeType(file);
        this.validateExtension(file);

        // If no exceptions were thrown during the checks, the file is valid
        return true;
    }

    /**
     * Handles the scenario where the file is either null or empty.
     * If it is required, it resolves the specific violation message and throws an exception.
     *
     * @param isNull true if the file is completely null, false if it is just empty.
     */
    private void handleMissingFile(boolean isNull) {
        if (!annotation.required()) {
            return;
        }

        String message = (isNull)
                ? this.resolveMessage(annotation.requiredMessage(), properties.getMessages().getRequired(), null)
                : this.resolveMessage(annotation.emptyMessage(), properties.getMessages().getEmpty(), null);

        throw new SecureFileValidationException(message);
    }

    /**
     * Validates the size of the uploaded file against the maximum allowed size.
     *
     * @param file represents the uploaded file to be validated.
     */
    private void validateSize(MultipartFile file) {
        long maxSizeBytes = annotation.maxSize() * 1024 * 1024;

        if (file.getSize() > maxSizeBytes) {
            String message = this.resolveMessage(annotation.tooLargeMessage(), properties.getMessages().getTooLarge(), file);
            throw new SecureFileValidationException(message);
        }
    }

    /**
     * Validates the MIME type of the uploaded file against the allowed MIME types.
     *
     * @param file represents the uploaded file to be validated.
     */
    private void validateMimeType(MultipartFile file) {
        String[] allowedMimeTypes = annotation.allowedMimeTypes();
        if (allowedMimeTypes == null || allowedMimeTypes.length == 0) {
            return;
        }

        String message = this.resolveMessage(annotation.invalidMimeTypeMessage(), properties.getMessages().getInvalidMimeType(), file);

        try (InputStream inputStream = file.getInputStream()) {
            String mimeType = tika.detect(inputStream);

            for (String allowedMimeType : allowedMimeTypes) {
                if (allowedMimeType.equalsIgnoreCase(mimeType)) {
                    return;
                }
            }

            throw new SecureFileValidationException(message);
        } catch (Exception e) {
            throw new SecureFileValidationException(message);
        }
    }

    /**
     * Checks if the provided filename is safe by ensuring it does not contain any directory traversal characters or null bytes.
     *
     * @param file represents the uploaded file whose filename is to be checked.
     */
    private void validateSafeFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String message = this.resolveMessage(annotation.unsafeFilenameMessage(), properties.getMessages().getUnsafeFilenameMessage(), file);

        if (!StringUtils.hasText(filename)) {
            throw new SecureFileValidationException(message);
        }

        String normalized = filename.replace('\\', '/');

        if (normalized.contains("/") || normalized.equals(".") || normalized.equals("..") || normalized.contains("\0")) {
            throw new SecureFileValidationException(message);
        }
    }

    /**
     * Validates the extension of the uploaded file against the allowed extensions.
     *
     * @param file represents the uploaded file to be validated.
     */
    private void validateExtension(MultipartFile file) {
        String[] allowedExtensions = annotation.allowedExtensions();
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return;
        }

        String message = this.resolveMessage(annotation.invalidExtensionMessage(), properties.getMessages().getInvalidExtension(), file);
        String fileName = file.getOriginalFilename();

        if (!StringUtils.hasText(fileName)) {
            throw new SecureFileValidationException(message);
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);

        if (fileExtension == null) {
            throw new SecureFileValidationException(message);
        }

        for (String allowedExtension : allowedExtensions) {
            if (allowedExtension.equalsIgnoreCase(fileExtension)) {
                return;
            }
        }

        throw new SecureFileValidationException(message);
    }

    /**
     * Resolves the validation message to be used.
     *
     * @param annotationMessage represents the message specified in the annotation.
     * @param defaultMessage    represents the default message specified in the application properties.
     * @param file              represents the uploaded file to be validated.
     * @return the resolved message to be used for validation.
     */
    private String resolveMessage(String annotationMessage, String defaultMessage, MultipartFile file) {
        return annotationMessage == null || annotationMessage.trim().isEmpty()
                ? this.formatMessage(defaultMessage, file)
                : this.formatMessage(annotationMessage, file);
    }

    /**
     * Replaces the "{filename}" placeholder with the actual file name.
     */
    private String formatMessage(String messageTemplate, MultipartFile file) {
        String filename = (file != null && file.getOriginalFilename() != null)
                ? file.getOriginalFilename()
                : "unknown";

        return messageTemplate.replace("{filename}", filename);
    }
}