package ru.abriel.ticket_analyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A DTO wrapper class that mirrors the root structure of the input JSON file.
 */
public record TicketsWrapperInputDto(
        @JsonProperty("tickets") List<TicketInputDto> tickets
) {}

