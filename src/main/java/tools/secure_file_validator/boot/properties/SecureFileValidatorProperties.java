package tools.secure_file_validator.boot.properties;

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
        private String required = "File is required.";
        private String unsafeFilenameMessage = "Filename contains unsafe characters.";
        private String empty = "File must not be empty.";
        private String tooLarge = "File exceeds the maximum allowed size.";
        private String invalidMimeType = "File MIME type is not allowed.";
        private String invalidExtension = "File extension is not allowed.";

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
