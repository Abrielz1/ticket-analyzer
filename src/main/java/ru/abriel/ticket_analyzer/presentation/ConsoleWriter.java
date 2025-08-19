package ru.abriel.ticket_analyzer.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Map;

/**
 * A utility component responsible for printing all application output to the console.
 * This class isolates all logging logic, ensuring that the core
 * services remain decoupled from the presentation layer. It acts as the final
 * "view" for this command-line application.
 */
@Slf4j
@Component
public class ConsoleWriter {

    /**
     * Prints a formatted error message to the standard error stream.
     *
     * @param message The non-null error message to be displayed.
     */
    public void printError(final String message) {
        log.error("[ERROR]: {}", message);
    }

    /**
     * Logs the final, formatted analysis results to the standard output stream.
     * The entire report is constructed and then logged at the INFO level. This allows the output
     * to be captured by standard logging configurations (console, file, etc.).
     *
     * @param minFlightTimes A Map where the key is the carrier name (String) and the value
     *                       is the minimum flight duration (Duration) for that carrier.
     *                       Cannot be null.
     * @param priceDifference The calculated difference between the average and median ticket prices.
     *                        Cannot be null.
     */
    public void printResults(final Map<String, Duration> minFlightTimes, final BigDecimal priceDifference) {

        BigDecimal displayDifference = priceDifference.setScale(2, RoundingMode.HALF_UP);

        log.info("Разница - %s%n".formatted(displayDifference));

        minFlightTimes.forEach((carrier, duration) -> {
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            log.info("%s - %dч %dм%n".formatted(carrier, hours, minutes));
        });
    }
}
