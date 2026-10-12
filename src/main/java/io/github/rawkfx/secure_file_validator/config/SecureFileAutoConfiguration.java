package io.github.rawkfx.secure_file_validator.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import io.github.rawkfx.secure_file_validator.properties.SecureFileValidatorProperties;

@Configuration
@EnableConfigurationProperties(SecureFileValidatorProperties.class)
public class SecureFileAutoConfiguration {
}
