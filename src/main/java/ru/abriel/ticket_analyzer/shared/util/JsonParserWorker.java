package ru.abriel.ticket_analyzer.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abriel.ticket_analyzer.domain.model.ticket.Ticket;
import java.io.InputStream;
import ru.abriel.ticket_analyzer.domain.model.ticket.TicketsWrapper;
import ru.abriel.ticket_analyzer.dto.TicketsWrapperInputDto;
import ru.abriel.ticket_analyzer.shared.exception.DataSourceNotFoundException;
import ru.abriel.ticket_analyzer.shared.exception.JsonParsingException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * Parses a JSON InputStream into a list of clean {@link Ticket} domain objects.
     *
     * @param dataStream The InputStream containing the JSON data.
     * @return A list of {@link Ticket} objects.
     * @throws JsonParsingException if any parsing or mapping error occurs.
     */
    public List<Ticket> parse(InputStream dataStream) {
        try {
            TicketsWrapperInputDto wrapperDto = objectMapper.readValue(dataStream, TicketsWrapperInputDto.class);
            if (wrapperDto == null || wrapperDto.tickets() == null) {
                throw new JsonParsingException("Root 'tickets' array is missing or null.", null);
            }

            return wrapperDto.tickets().stream()
                    .map(TicketMapper::fromDto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new JsonParsingException("Failed to parse JSON stream. Check for malformed data.", e);
        }
    }

    /**
     * Parses an InputStream containing ticket data in JSON format.
     *
     * @param dataStream The active InputStream to read the JSON data from.
     *                   The stream will be closed by the caller (e.g., via try-with-resources).
     * @return A {@link List} of {@link Ticket} domain objects.
     *         Returns an empty list if the input is empty or invalid.
     * @throws RuntimeException if a critical, unrecoverable parsing error occurs.
     */
    public List<Ticket> parser(final InputStream dataStream) {
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
