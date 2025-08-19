package ru.abriel.ticket_analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.abriel.ticket_analyzer.domain.model.ticket.DataProviderService;
import ru.abriel.ticket_analyzer.domain.model.ticket.Ticket;
import ru.abriel.ticket_analyzer.domain.model.ticket.TicketDocument;
import ru.abriel.ticket_analyzer.repository.TicketDocumentRepository;
import ru.abriel.ticket_analyzer.shared.exception.DataSourceNotFoundException;
import ru.abriel.ticket_analyzer.shared.util.JsonParserWorker;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link DataProviderService}.
 * This service implements the core data-sourcing logic pipeline for the application.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataProviderServiceImpl implements DataProviderService {

    private final TicketDocumentRepository ticketRepository;
    private final JsonParserWorker jsonParser;
    private final ResourceLoader resourceLoader;
    private static final String DEFAULT_RESOURCE_PATH = "classpath:input_data/base_data.json";
    @Override
    public List<Ticket> getData(final Optional<Path> filePathOpt) {
        return filePathOpt
                .map(this::loadFromFileAndSaveToMongo)
                .or(this::loadFromMongo)
                .orElseGet(this::loadFromDefaultResourceAndSaveToMongo);
    }

    private Optional<List<Ticket>> loadFromMongo() {
        log.info("Strategy: No file provided. Checking for cached data in MongoDB.");
        return ticketRepository.findTopByOrderByUploadedAtDesc()
                .map(ticketDocument -> {
                    log.info("Strategy: Data found in MongoDB. Using cached version.");
                    return ticketDocument.tickets();
                });
    }

    private List<Ticket> loadFromFileAndSaveToMongo(final Path path) {
        log.info("Strategy: Loading data from user-provided file: {}", path);
        try (InputStream stream = Files.newInputStream(path)) {
           final List<Ticket> tickets = jsonParser.parse(stream);
            this.saveToMongo(tickets);
            return tickets;
        } catch (Exception e) {
            throw new DataSourceNotFoundException("Failed to process user-provided file: " + path + e.getMessage());
        }
    }

    private List<Ticket> loadFromDefaultResourceAndSaveToMongo() {
        log.warn("Strategy: No data in MongoDB. Performing cold start from default resource.");
        try {
           final Resource resource = resourceLoader.getResource(DEFAULT_RESOURCE_PATH);
            if (!resource.exists()) {
                throw new FileNotFoundException("Default resource file not found in build: " + DEFAULT_RESOURCE_PATH);
            }
            try (InputStream stream = resource.getInputStream()) {
                List<Ticket> tickets = jsonParser.parse(stream);
                this.saveToMongo(tickets);
                return tickets;
            }
        } catch (Exception e) {
            throw new DataSourceNotFoundException("Critical error during cold start from resource" + e.getMessage());
        }
    }

    private void saveToMongo(final List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            log.warn("Parsed ticket list is empty. Skipping save to MongoDB.");
            return;
        }
        log.info("Saving {} parsed tickets to MongoDB...", tickets.size());
        ticketRepository.save(new TicketDocument(null, Instant.now(), tickets));
        log.info("Data successfully saved to MongoDB.");
    }
}