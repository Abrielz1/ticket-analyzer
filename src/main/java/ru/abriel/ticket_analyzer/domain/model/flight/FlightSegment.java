package ru.abriel.ticket_analyzer.domain.model.flight;

import java.time.ZonedDateTime;

/**
 * A Value Object representing a single flight segment (one leg of a journey).
 * Connects two airports with their respective, timezone-aware departure and arrival times.
 * @param origin The origin airport information object.
 * @param departure The exact, timezone-aware departure time.
 * @param destination The destination airport information object.
 * @param arrival The exact, timezone-aware arrival time.
 */
public record FlightSegment(
        AirportInfo origin,
        ZonedDateTime departure,
        AirportInfo destination,
        ZonedDateTime arrival
) {}