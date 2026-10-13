package io.github.rawkfx.secure_file_validator.annotation;

import io.github.rawkfx.secure_file_validator.validator.SecureFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SecureFileValidator.class)
@Documented
public @interface SecureFile {
    boolean required() default true;

    String message() default "Invalid file.";

    long maxSize() default 50;

    String[] allowedMimeTypes() default {};

    String[] allowedExtensions() default {};

    String requiredMessage() default "";

    String unsafeFilenameMessage() default "";

    String emptyMessage() default "";

    String tooLargeMessage() default "";

    String invalidMimeTypeMessage() default "";

    String invalidExtensionMessage() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
