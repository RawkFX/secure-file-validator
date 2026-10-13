package io.github.rawkfx.secure_file_validator.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "secure-file-validator")
public class SecureFileValidatorProperties {
    private Messages messages = new Messages();

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public static class Messages {
        private String required = "File '{filename}' is required.";
        private String unsafeFilenameMessage = "File name '{filename}' contains unsafe characters.";
        private String empty = "File '{filename}' must not be empty.";
        private String tooLarge = "File '{filename}' exceeds the maximum allowed size.";
        private String invalidMimeType = "File '{filename}' MIME type is not allowed.";
        private String invalidExtension = "File '{filename}' extension is not allowed.";

        public String getRequired() {
            return required;
        }

        public void setRequired(String required) {
            this.required = required;
        }

        public String getUnsafeFilenameMessage() {
            return unsafeFilenameMessage;
        }

        public void setUnsafeFilenameMessage(String unsafeFilenameMessage) {
            this.unsafeFilenameMessage = unsafeFilenameMessage;
        }

        public String getEmpty() {
            return empty;
        }

        public void setEmpty(String empty) {
            this.empty = empty;
        }

        public String getTooLarge() {
            return tooLarge;
        }

        public void setTooLarge(String tooLarge) {
            this.tooLarge = tooLarge;
        }

        public String getInvalidMimeType() {
            return invalidMimeType;
        }

        public void setInvalidMimeType(String invalidMimeType) {
            this.invalidMimeType = invalidMimeType;
        }

        public String getInvalidExtension() {
            return invalidExtension;
        }

        public void setInvalidExtension(String invalidExtension) {
            this.invalidExtension = invalidExtension;
        }
    }
}
