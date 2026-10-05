package tools.secure_file_validator.boot.validator;


import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tools.secure_file_validator.boot.annotation.SecureFile;
import tools.secure_file_validator.boot.properties.SecureFileValidatorProperties;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;

/**
 * Validator for the @SecureFile annotation. Validates the uploaded file based on size, MIME type, and extension.
 * It uses Apache Tika to detect the MIME type of the file.
 * The validation messages can be customized through the annotation or application properties.
 */
@Component
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

        if(isNull || isEmpty) {
            return this.handleMissingFile(constraintValidatorContext, isNull);
        }

        if (!this.isSafeFilename(file, constraintValidatorContext, this.resolveMessage(annotation.unsafeFilenameMessage(), properties.getMessages().getUnsafeFilenameMessage()))) {
            return false;
        }

        if (!this.isValidSize(file, constraintValidatorContext, annotation.maxSizeMb(), this.resolveMessage(annotation.tooLargeMessage(), properties.getMessages().getTooLarge()))) {
            return false;
        }

        if (!this.isValidMimeType(file, constraintValidatorContext, annotation.allowedMimeTypes(), this.resolveMessage(annotation.invalidMimeTypeMessage(), properties.getMessages().getInvalidMimeType()))) {
            return false;
        }

        if (!this.isValidExtension(file, constraintValidatorContext, annotation.allowedExtensions(), this.resolveMessage(annotation.invalidExtensionMessage(), properties.getMessages().getInvalidExtension()))) {
            return false;
        }

        return true;
    }

    /**
     * Handles the scenario where the file is either null or empty.
     * If the file is not required, it passes validation.
     * If it is required, it resolves the specific violation message and fails validation.
     *
     * @param context represents the context in which the validation is being performed.
     * @return true if the missing file is optional, false if it is required.
     */
    private boolean handleMissingFile(ConstraintValidatorContext context, boolean isNull) {
        if (!annotation.required()) {
            return true;
        }

        String message = (isNull)
                ? this.resolveMessage(annotation.requiredMessage(), properties.getMessages().getRequired())
                : this.resolveMessage(annotation.emptyMessage(), properties.getMessages().getEmpty());

        return this.addViolation(context, message);
    }

    /**
     * Validates the size of the uploaded file against the maximum allowed size.
     *
     * @param file      represents the uploaded file to be validated.
     * @param context   represents the context in which the validation is being performed.
     * @param maxSizeMb represents the maximum allowed size of the file in megabytes.
     * @param message   represents the message to be displayed if the file size exceeds the maximum allowed size.
     * @return true if the file size is valid, false otherwise.
     */
    private boolean isValidSize(MultipartFile file, ConstraintValidatorContext context, long maxSizeMb, String message) {
        long maxSizeBytes = maxSizeMb * 1024 * 1024;

        if (file.getSize() > maxSizeBytes) {
            return this.addViolation(context, message);
        }

        return true;
    }

    /**
     * Validates the MIME type of the uploaded file against the allowed MIME types.
     *
     * @param file             represents the uploaded file to be validated.
     * @param context          represents the context in which the validation is being performed.
     * @param allowedMimeTypes represents the array of allowed MIME types for the file.
     * @param message          represents the message to be displayed if the file's MIME type is not allowed.
     * @return true if the file's MIME type is valid, false otherwise.
     */
    private boolean isValidMimeType(MultipartFile file, ConstraintValidatorContext context, String[] allowedMimeTypes, String message) {
        if (allowedMimeTypes == null || allowedMimeTypes.length == 0) {
            return true;
        }

        try (InputStream inputStream = file.getInputStream()) {
            String mimeType = tika.detect(inputStream);

            for (String allowedMimeType : allowedMimeTypes) {
                if (allowedMimeType.equalsIgnoreCase(mimeType)) {
                    return true;
                }
            }

            return this.addViolation(context, message);
        } catch (Exception e) {
            return this.addViolation(context, message);
        }
    }

    /**
     * Checks if the provided filename is safe by ensuring it does not contain any directory traversal characters or null bytes.
     *
     * @param file    represents the uploaded file whose filename is to be checked.
     * @param context represents the context in which the validation is being performed.
     * @param message represents the message to be displayed if the filename is not safe.
     * @return true if the filename is safe, false otherwise.
     */
    private boolean isSafeFilename(MultipartFile file, ConstraintValidatorContext context, String message) {
        String filename = file.getOriginalFilename();

        if (!StringUtils.hasText(filename)) {
            return this.addViolation(context, message);
        }

        String normalized = filename.replace('\\', '/');

        if (normalized.contains("/") || normalized.equals(".") || normalized.equals("..") || normalized.contains("\0")) {
            return this.addViolation(context, message);
        }

        return true;
    }

    /**
     * Validates the extension of the uploaded file against the allowed extensions.
     *
     * @param file              represents the uploaded file to be validated.
     * @param context           represents the context in which the validation is being performed.
     * @param allowedExtensions represents the array of allowed extensions for the file.
     * @param message           represents the message to be displayed if the file's extension is not allowed.
     * @return true if the file's extension is valid, false otherwise.
     */
    private boolean isValidExtension(MultipartFile file, ConstraintValidatorContext context, String[] allowedExtensions, String message) {
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return true;
        }

        String fileName = file.getOriginalFilename();

        if (!StringUtils.hasText(fileName)) {
            return this.addViolation(context, message);
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);

        if (fileExtension == null) {
            return this.addViolation(context, message);
        }

        for (String allowedExtension : allowedExtensions) {
            if (allowedExtension.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }

        return this.addViolation(context, message);
    }

    /**
     * Adds a violation message to the constraint validator context and disables the default violation.
     *
     * @param context represents the context in which the validation is being performed.
     * @param message represents the message to be added as a violation.
     * @return false to indicate that the validation has failed.
     */
    private boolean addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

        return false;
    }

    /**
     * Resolves the validation message to be used.
     *
     * @param annotationMessage represents the message specified in the annotation.
     * @param defaultMessage    represents the default message specified in the application properties.
     * @return the resolved message to be used for validation.
     */
    private String resolveMessage(String annotationMessage, String defaultMessage) {
        return annotationMessage == null || annotationMessage.isBlank()
                ? defaultMessage
                : annotationMessage;
    }
}
