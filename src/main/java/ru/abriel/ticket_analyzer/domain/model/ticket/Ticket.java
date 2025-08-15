package ru.abriel.ticket_analyzer.domain.model.ticket;

import ru.abriel.ticket_analyzer.domain.model.flight.FlightSegment;
import ru.abriel.ticket_analyzer.domain.model.money.Price;
import java.util.List;

/**
 * The main domain entity, representing a single airline ticket.
 * Acts as an Aggregate Root that combines all information about a journey.
 * @param price The Price value object for the ticket.
 * @param carrierCode The IATA code of the airline carrier (e.g., "S7").
 * @param carrierName The name of the airline carrier.
 * @param segments A list of flight segments. Can contain one segment (for a direct flight)
 *                 or multiple segments (for a connecting flight).
 */
public record Ticket(
        Price price,
        String carrierCode,
        String carrierName,
        List<FlightSegment> segments
) {}