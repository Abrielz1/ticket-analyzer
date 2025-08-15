package ru.abriel.ticket_analyzer.domain.model.flight;

import ru.abriel.ticket_analyzer.domain.model.geography.GeoPoint;
import java.time.ZoneId;

/**
 * A Value Object containing all relevant information about an airport.
 * @param code The IATA code of the airport (e.g., "VVO").
 * @param city The name of the city where the airport is located.
 * @param timezone The official timezone of the airport.
 * @param location The geographic coordinates of the airport.
 */
public record AirportInfo(String code, String city, ZoneId timezone, GeoPoint location) {}
