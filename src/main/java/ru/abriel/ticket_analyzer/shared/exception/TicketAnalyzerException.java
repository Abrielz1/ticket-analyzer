package ru.abriel.ticket_analyzer.shared.exception;

/**
 * The base, su_a, unchecked exception for all custom business errors in the application.
 */
public class TicketAnalyzerException extends RuntimeException {
    public TicketAnalyzerException(String message) {
        super(message);
    }

    public TicketAnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}