package io.github.rawkfx.secure_file_validator.config;

import io.github.rawkfx.secure_file_validator.handler.SecureFileExceptionHandler;
import io.github.rawkfx.secure_file_validator.properties.SecureFileValidatorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(SecureFileValidatorProperties.class)
@Import({TikaAutoConfiguration.class, SecureFileExceptionHandler.class})
public class SecureFileAutoConfiguration {
}
