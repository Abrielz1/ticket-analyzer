package ru.abriel.ticket_analyzer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;
import ru.abriel.ticket_analyzer.service.FlightAnalysisService;
import ru.abriel.ticket_analyzer.shared.exception.DataSourceNotFoundException;
import ru.abriel.ticket_analyzer.shared.exception.JsonParsingException;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * The main Picocli command for the ticket analysis application.
 * This class is the primary boundary between the user's command-line input and the application's core logic.
 */
@Slf4j
@Component
@Command(name = "analyze",
        mixinStandardHelpOptions = true,
        version = "Ticket Analyzer 1.0",
        description = "Analyzes ticket data using a multi-layered data sourcing strategy.")
@RequiredArgsConstructor
public class AnalyzeTicketsCommand implements Callable<Integer>, ExitCodeGenerator {

    private final FlightAnalysisService analysisService;
    private int exitCode;

    @Parameters(index = "0", description = "Path to the tickets JSON file. If omitted, uses the default data source pipeline.", arity = "0..1")
    private Path filePath;

    @Parameters(index = "1", description = "Origin city name.")
    private String originCity;

    @Parameters(index = "2", description = "Destination city name.")
    private String destinationCity;

    @Override
    public Integer call() {
        try {
            log.info("Analysis command initiated for route: {} -> {}", originCity, destinationCity);
            // This class's only job is to delegate. It knows nothing about how the data is retrieved or processed.
            analysisService.analyzeAndPrintResults(Optional.ofNullable(filePath), originCity, destinationCity);
            log.info("Analysis command completed successfully.");
            this.exitCode = ExitCode.OK;
        } catch (DataSourceNotFoundException e) {
            log.error("Data source error: {}", e.getMessage());
            System.err.println("ERROR: Could not find ticket data. Please provide a valid file path or ensure the default data is available.");
            this.exitCode = ExitCode.USAGE;
        } catch (JsonParsingException e) {
            log.error("Data format error: Failed to parse input JSON.", e);
            System.err.println("ERROR: The provided JSON file is malformed or unreadable.");
            this.exitCode = ExitCode.USAGE;
        } catch (Exception e) {
            log.error("An unexpected critical error occurred during the analysis process.", e);
            System.err.println("CRITICAL ERROR: An unexpected internal error has occurred. Please check the logs for more details.");
            this.exitCode = ExitCode.SOFTWARE;
        }
        return this.exitCode;
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }
}