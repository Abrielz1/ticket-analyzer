package ru.abriel.ticket_analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import ru.abriel.ticket_analyzer.command.AnalyzeTicketsCommand;

/**
 * The main entry point for the Spring Boot application.
 * This class is responsible for initializing the Spring context and delegating the
 * command-line execution to the Picocli framework. It also ensures that the
 * application's exit code is correctly propagated from the command's execution result.
 */
@SpringBootApplication
@RequiredArgsConstructor
public class TicketAnalyzerApplication implements CommandLineRunner, ExitCodeGenerator {

	/**
	 * The main Picocli command bean, injected by Spring.
	 * This is the root of all command-line logic.
	 */
	private final AnalyzeTicketsCommand analyzeCommand;

	/**
	 * Stores the exit code returned by the Picocli command execution.
	 */
	private int exitCode;

	/**
	 * The standard main method that bootstraps the Spring Boot application.
	 *
	 * @param args Command line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(TicketAnalyzerApplication.class, args);
	}

	/**
	 * The core execution logic that runs after the Spring application context is fully loaded.
	 * It hands over control to the Picocli command-line parser.
	 *
	 * @param args The command line arguments.
	 */
	@Override
	public void run(String... args) throws Exception {
		new CommandLine(this.analyzeCommand).execute(args);
	}

	/**
	 * Provides the final exit code to the Spring Boot application context.
	 * Spring Boot will automatically call this method upon shutdown.
	 *
	 * @return The integer exit code from the last command execution.
	 */
	@Override
	public int getExitCode() {
		return this.exitCode;
	}
}
