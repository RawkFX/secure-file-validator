# Secure File Validator

Secure File Validator is a Spring Boot validation utility for file uploads. It helps prevent unsafe or invalid files from entering your application by checking whether a file is present, not empty, within the allowed size, has a safe filename, and matches the allowed MIME type and extension.

Why use it?
- Basic validation for upload forms and REST endpoints.
- Protection against empty or missing files.
- Protection against oversized uploads.
- Prevents path-like or unsafe filenames.
- Enforces expected file types and extensions.
- Supports custom validation messages without changing Java code.

How it works
- Add the annotation `@SecureFile` to a `MultipartFile` field or parameter.
- Configure basic rules such as `required`, `maxSizeMb`, `allowedMimeTypes`, and `allowedExtensions`.
- Spring Validation processes the annotation automatically.
- The validator uses Apache Tika to detect the MIME type of the uploaded file.

Example usage
```java
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.secure_file_validator.boot.annotation.SecureFile;

public class UploadRequest {

    @SecureFile(
        required = true,
        maxSizeMb = 10,
        allowedMimeTypes = {"image/png", "application/pdf"},
        allowedExtensions = {"png", "pdf"}
    )
    private MultipartFile file;
}

@RestController
public class UploadController {

    @PostMapping("/upload")
    public String upload(@Valid @ModelAttribute UploadRequest request) {
        return "OK";
    }
}
```

The library also supports per-field override messages directly on the annotation:
- `message`
- `requiredMessage`
- `unsafeFilenameMessage`
- `emptyMessage`
- `tooLargeMessage`
- `invalidMimeTypeMessage`
- `invalidExtensionMessage`

If a specific annotation message is blank or not provided, the application property value is used as the default.

Configurable messages in the application properties file
The project exposes default validation messages under the `secure-file-validator.messages` prefix.

Example `application.yaml`
```yaml
secure-file-validator:
  messages:
    required: "A file is required."
    unsafeFilenameMessage: "The file name contains unsafe characters."
    empty: "The uploaded file is empty."
    tooLarge: "The file exceeds the maximum allowed size."
    invalidMimeType: "This file type is not allowed."
    invalidExtension: "This file extension is not allowed."
```

Equivalent `application.properties`
```properties
secure-file-validator.messages.required=A file is required.
secure-file-validator.messages.unsafeFilenameMessage=The file name contains unsafe characters.
secure-file-validator.messages.empty=The uploaded file is empty.
secure-file-validator.messages.tooLarge=The file exceeds the maximum allowed size.
secure-file-validator.messages.invalidMimeType=This file type is not allowed.
secure-file-validator.messages.invalidExtension=This file extension is not allowed.
```

These messages correspond to the built-in validation rules:
- `required`: no file was uploaded when the field is required
- `unsafeFilenameMessage`: filename contains forbidden path characters or traversal patterns
- `empty`: file is present but empty
- `tooLarge`: file size exceeds `maxSizeMb`
- `invalidMimeType`: detected MIME type is not permitted
- `invalidExtension`: file extension is not permitted

Notes
- Annotation-level messages take priority over property-based messages.
- If you keep the annotation values empty, the app property defaults are used automatically.
- This repository currently targets Spring Boot 4.x on the `main` branch; older 2.x code is kept on the `2.x` branch.
