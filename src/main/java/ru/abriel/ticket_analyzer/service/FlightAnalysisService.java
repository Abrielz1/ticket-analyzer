package ru.abriel.ticket_analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abriel.ticket_analyzer.domain.model.geography.GeoPoint;
import ru.abriel.ticket_analyzer.domain.model.money.Price;
import ru.abriel.ticket_analyzer.domain.model.ticket.DataProviderService;
import ru.abriel.ticket_analyzer.domain.model.ticket.Ticket;
import ru.abriel.ticket_analyzer.presentation.ConsoleWriter;
import ru.abriel.ticket_analyzer.shared.util.GeoCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightAnalysisService {

    private final DataProviderService dataProvider;

    private final GeoCalculator geoCalculator;

    private final ConsoleWriter consoleWriter;

    public void analyzeAndPrintResults(final Optional<Path> filePath, final String originCity, final String destinationCity) {
        log.info("Analysis process started.");

        final List<Ticket> tickets = dataProvider.getData(filePath);
        if (tickets.isEmpty()) { /* ... */ return; }
        log.info("Data received. Filtering for route: {} -> {}", originCity, destinationCity);

        final List<Ticket> relevantTickets = tickets.stream()
                .filter(t -> !t.segments().isEmpty() &&
                        originCity.equalsIgnoreCase(t.segments().get(0).origin().city()) &&
                        destinationCity.equalsIgnoreCase(t.segments().get(t.segments().size() - 1).destination().city()))
                .collect(Collectors.toList());

        if (relevantTickets.isEmpty()) { /* ... */ return; }
        log.info("Found {} relevant tickets. Calculating metrics...", relevantTickets.size());

        final Map<String, Duration> minJourneyTimes = calculateMinJourneyTimes(relevantTickets);

        final BigDecimal priceDifference = calculatePriceDifference(relevantTickets);

        consoleWriter.printResults(minJourneyTimes, priceDifference);

        log.info("Analysis process finished successfully.");
    }

    /**
     * Calculates the minimum total journey duration for each carrier.
     * This is the primary calculation for the TDD.
     */
    private Map<String, Duration> calculateMinJourneyTimes(final List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::carrierName,
                        Collectors.mapping(
                                this::calculateTotalDuration,
                                Collectors.minBy(Duration::compareTo)
                        )
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    private Duration calculateTotalDuration(final Ticket ticket) {

        final ZonedDateTime departure = ticket.segments().get(0).departure();
        final ZonedDateTime arrival = ticket.segments().get(ticket.segments().size() - 1).arrival();
        final Duration journeyDuration = Duration.between(departure, arrival);

        final GeoPoint originPoint = ticket.segments().get(0).origin().location();
        final GeoPoint destinationPoint = ticket.segments().get(ticket.segments().size() - 1).destination().location();
        final Duration estimatedAirTime = geoCalculator.estimateFlightTime(originPoint, destinationPoint);
        log.debug("Ticket from carrier '{}': Journey time = {}, Estimated pure air time = {}",
                ticket.carrierName(), journeyDuration, estimatedAirTime);

        return journeyDuration;
    }

    private BigDecimal calculatePriceDifference(final List<Ticket> tickets) {
        final List<BigDecimal> prices = tickets.stream()
                .map(Ticket::price)
                .map(Price::amount)
                .sorted()
                .toList();
        if (prices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        final BigDecimal sum = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal average = sum.divide(new BigDecimal(prices.size()), 2, RoundingMode.HALF_UP);
        final BigDecimal median;
        final int size = prices.size();

        if (size % 2 == 0) {
            median = prices.get(size / 2 - 1).add(prices.get(size / 2)).divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
        } else {
            median = prices.get(size / 2);
        }
        return average.subtract(median).abs();
    }
}