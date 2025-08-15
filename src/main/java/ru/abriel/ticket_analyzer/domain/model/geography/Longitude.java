package ru.abriel.ticket_analyzer.domain.model.geography;

/**
 * A Value Object representing the longitude of a geographic point.
 * Ensures the value is always within the valid range [-180.0, 180.0].
 * @param value The longitude value in degrees.
 */
public record Longitude(double value) {
    public Longitude {
        if (value < -180.0 || value > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180.0 and 180.0, but was: " + value);
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