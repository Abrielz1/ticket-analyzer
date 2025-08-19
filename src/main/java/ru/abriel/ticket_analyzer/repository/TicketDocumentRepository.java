package ru.abriel.ticket_analyzer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.abriel.ticket_analyzer.domain.model.ticket.TicketDocument;
import java.util.Optional;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

/**
 * Spring Data repository interface for accessing {@link TicketDocument} objects
 * stored in MongoDB.
 * <p>
 * This interface provides the primary abstraction for all database operations
 * related to ticket data batches.
 */
@Repository
public interface TicketDocumentRepository extends MongoRepository<TicketDocument, String> {

    /**
     * Finds the most recently uploaded ticket document in the collection.
     * <p>
     * This method leverages Spring Data's query derivation mechanism. It works by
     * ordering all documents by the {@code uploadedAt} field in descending order
     * and retrieving only the first result. This is the core mechanism for fetching
     * the most current dataset for analysis.
     *
     * @return An {@link Optional} containing the latest {@link TicketDocument},
     *         or an empty Optional if the collection is empty.
     */
    Optional<TicketDocument> findTopByOrderByUploadedAtDesc();
}
