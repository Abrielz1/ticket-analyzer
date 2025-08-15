package ru.abriel.ticket_analyzer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global application configuration class.
 * This class is responsible for creating and configuring shared beans
 * that are used across the entire application.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Creates a singleton bean of the Jackson {@link ObjectMapper}.
     * <p>
     * This method provides a centrally configured ObjectMapper instance for the entire
     * Spring application context. By defining it here, we ensure that any component
     * that needs to parse JSON can simply autowire this bean instead of creating
     * its own instance.
     * <p>
     * It is pre-configured with the {@link JavaTimeModule} to correctly
     * serialize and deserialize modern Java date/time types (e.g., ZonedDateTime).
     *
     * @return A configured ObjectMapper instance.
     */
    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}