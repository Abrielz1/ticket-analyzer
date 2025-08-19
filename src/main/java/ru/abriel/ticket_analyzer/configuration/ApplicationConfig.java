package ru.abriel.ticket_analyzer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

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

    /**
     * Creates a MongoCustomConversions bean to teach Spring Data MongoDB how to handle
     * complex or unsupported types like {@link ZonedDateTime}.
     *
     * @return A {@link MongoCustomConversions} object containing all custom converters.
     */
    @Bean
    public MongoCustomConversions customMongoConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new ZonedDateTimeToDateConverter(),
                new DateToZonedDateTimeConverter()
        ));
    }

    /**
     * Converter to write ZonedDateTime objects into the database as standard UTC Dates.
     */
    private static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    /**
     * Converter to read standard Dates from the database and convert them back to ZonedDateTime at UTC.
     */
    private static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : source.toInstant().atZone(ZoneOffset.UTC);
        }
    }
}