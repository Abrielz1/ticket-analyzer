package ru.abriel.ticket_analyzer.domain.model.ticket;

import java.util.List;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Contract for the service responsible for providing ticket data.
 * This service encapsulates the complex logic of finding the correct data source
 * for the analysis, following a "Chain of Responsibility" pattern.
 */
public interface DataProviderService {

    /**
     * Retrieves ticket data based on the defined multi-layered strategy.
     * The method attempts to find data in the following order of priority:
     * <ol>
     *     <li>From the user-provided file path, if present. This data is then persisted to MongoDB.</li>
     *     <li>From the MongoDB cache (the most recently uploaded document).</li>
     *     <li>From the default internal resource file ({@code classpath:input_data/base_data.json}) as a last resort
     *     during a "cold start". This data is also persisted to MongoDB.</li>
     * </ol>
     *
     * @param filePath An {@link Optional} containing the user-provided {@link Path} to a JSON file.
     *                 If empty, the service will proceed to check MongoDB and internal resources.
     * @return A list of {@link Ticket} objects ready for analysis. Returns an empty list if no data can be found.
     */
    List<Ticket> getData(final Optional<Path> filePath);
}
