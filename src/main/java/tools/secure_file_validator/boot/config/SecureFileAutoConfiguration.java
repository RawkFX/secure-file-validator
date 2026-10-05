package tools.secure_file_validator.boot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tools.secure_file_validator.boot.properties.SecureFileValidatorProperties;

@Configuration
@EnableConfigurationProperties(SecureFileValidatorProperties.class)
public class SecureFileAutoConfiguration {
}
