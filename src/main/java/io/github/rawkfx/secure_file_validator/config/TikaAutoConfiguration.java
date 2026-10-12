package io.github.rawkfx.secure_file_validator.config;

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
