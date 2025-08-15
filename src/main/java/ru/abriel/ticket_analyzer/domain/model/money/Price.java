package ru.abriel.ticket_analyzer.domain.model.money;

import java.math.BigDecimal;

/**
 * A Value Object for representing monetary values.
 * Uses BigDecimal for absolute precision, as required for financial calculations.
 * @param amount The monetary amount.
 * @param currency The three-letter currency code (e.g., "RUB", "USD").
 */
public record Price(BigDecimal amount, String currency) {}
