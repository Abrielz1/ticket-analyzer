package ru.abriel.ticket_analyzer.shared.exception;

/**
 * Thrown when the application cannot find ANY data source to proceed.
 * This indicates that the user did not provide a file, the MongoDB cache is empty,
 * AND the default internal resource is missing. It's a total, su_a, failure.
 */
public class DataSourceNotFoundException extends TicketAnalyzerException {
    public DataSourceNotFoundException(String message) {
        super(message);
    }
}
