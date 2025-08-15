package ru.abriel.ticket_analyzer.shared.exception;

/**
 * Thrown when a critical, unrecoverable error occurs during JSON parsing.
 */
public class JsonParsingException extends TicketAnalyzerException {
    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
