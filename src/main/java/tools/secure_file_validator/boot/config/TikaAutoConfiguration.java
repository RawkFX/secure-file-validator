package tools.secure_file_validator.boot.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaAutoConfiguration {
    @Bean
    public Tika tika() {
        return new Tika();
    }
}
