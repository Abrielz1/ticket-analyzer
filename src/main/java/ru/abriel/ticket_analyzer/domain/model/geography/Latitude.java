package ru.abriel.ticket_analyzer.domain.model.geography;

/**
 * A Value Object representing the latitude of a geographic point.
 * Ensures the value is always within the valid range [-90.0, 90.0].
 * @param value The latitude value in degrees.
 */
public record Latitude(double value) {
    public Latitude {
        if (value < -90.0 || value > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90.0 and 90.0, but was: " + value);
        }
    }

    /**
     * Converts the degree value to radians for trigonometric calculations.
     * @return The value in radians.
     */
    public double toRadians() {
        return Math.toRadians(value);
    }
}