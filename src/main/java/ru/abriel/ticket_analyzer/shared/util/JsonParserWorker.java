package ru.abriel.ticket_analyzer.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abriel.ticket_analyzer.domain.model.ticket.Ticket;
import java.io.InputStream;
import ru.abriel.ticket_analyzer.domain.model.ticket.TicketsWrapper;
import ru.abriel.ticket_analyzer.shared.exception.DataSourceNotFoundException;
import java.util.List;

/**
 * A utility component responsible for parsing JSON data streams into domain models.
 * This class encapsulates all logic related to the Jackson library and the
 * specific structure of the input JSON file.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonParserWorker {

    /**
     * The configured Jackson ObjectMapper instance.
     * It is pre-configured to handle Java 8 Time API (e.g., ZonedDateTime).
     */
    private final ObjectMapper objectMapper;

    /**
     * Parses an InputStream containing ticket data in JSON format.
     *
     * @param dataStream The active InputStream to read the JSON data from.
     *                   The stream will be closed by the caller (e.g., via try-with-resources).
     * @return A {@link List} of {@link Ticket} domain objects.
     *         Returns an empty list if the input is empty or invalid.
     * @throws RuntimeException if a critical, unrecoverable parsing error occurs.
     */
    public List<Ticket> parse(final InputStream dataStream) {
        try {
            final TicketsWrapper wrapper = objectMapper.readValue(dataStream, TicketsWrapper.class);
            log.info("[JsonParser]: Успешно распарсил " + wrapper.tickets().size() + " билетов.");
            return wrapper.tickets();
        } catch (Exception e) {
            log.error("Could not find any ticket data source (File, MongoDB, or internal resource)");
            throw new DataSourceNotFoundException("Could not find any ticket data source (File, MongoDB, or internal resource)");
        }
    }
}
