package ru.abriel.ticket_analyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record TicketInputDto(
    String origin,
    @JsonProperty("origin_name") String originName,
    String destination,
    @JsonProperty("destination_name") String destinationName,
    @JsonProperty("departure_date") String departureDate, // Parsing as String first
    @JsonProperty("departure_time") String departureTime,
    @JsonProperty("arrival_date") String arrivalDate,
    @JsonProperty("arrival_time") String arrivalTime,
    String carrier,
    int stops,
    BigDecimal price
        )
{}
