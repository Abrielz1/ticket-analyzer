package ru.abriel.ticket_analyzer.domain.model.ticket;

import java.util.List;

/**
 * A helper wrapper class used ONLY for JSON parsing.
 * Its structure mirrors the root of the input JSON file.
 * @param tickets The list of tickets parsed from the file.
 */
public record TicketsWrapper(List<Ticket> tickets) {}