package ru.abriel.ticket_analyzer.shared.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.abriel.ticket_analyzer.domain.model.geography.GeoPoint;
import java.time.Duration;

/**
 * A utility component for performing geographical calculations.
 * This class encapsulates complex mathematical formulas for working with geodetic coordinates.
 */
@Component
public class GeoCalculator {

    /**
     * The mean radius of Earth in meters, used for distance calculations.
     */
    private static final double EARTH_RADIUS_METERS = 6372795.0;

    /**
     * The average cruise speed of a commercial aircraft in km/h.
     * This value can be configured in the application.yml file using the key {@code app.avg-cruise-speed-kmph}.
     */
    @Value("${app.avg-cruise-speed-kmph:850.0}")
    private double averageCruiseSpeedKmph;

    /**
     * Estimates the pure flight time between two geographic points.
     * <p>
     * The calculation is based on the great-circle distance (Haversine formula)
     * and a configurable average cruise speed. This method represents the "in-air"
     * time and does not account for taxiing, takeoff, or landing procedures.
     *
     * @param origin The starting {@link GeoPoint}. Cannot be null.
     * @param destination The ending {@link GeoPoint}. Cannot be null.
     * @return An estimated flight {@link Duration}.
     */
    public Duration estimateFlightTime(final GeoPoint origin, final GeoPoint destination) {
        final double distanceMeters = this.calculateDistanceMeters(origin, destination);
        final double distanceKm = distanceMeters / 1000.0;
        final double hours = distanceKm / averageCruiseSpeedKmph;
        return Duration.ofMinutes((long) (hours * 60));
    }

    /**
     * Calculates the great-circle distance between two points on the Earth's surface
     * using the Haversine formula.
     * <p>
     * This is a private helper method that contains the core mathematical logic.
     * All parameters are expected to be valid and non-null.
     *
     * @param origin The starting {@link GeoPoint}, containing longitude and latitude.
     * @param destination The ending {@link GeoPoint}, containing longitude and latitude.
     * @return The distance in meters as a double.
     */
    private double calculateDistanceMeters(final GeoPoint origin, final GeoPoint destination) {
        // Convert all degree-based values to radians for trigonometric functions.
        final double lat1Rad = origin.latitude().toRadians();
        final double lon1Rad = origin.longitude().toRadians();
        final double lat2Rad = destination.latitude().toRadians();
        final double lon2Rad = destination.longitude().toRadians();

        // Pre-calculate sines and cosines for performance and readability.
        final double cosLat1 = Math.cos(lat1Rad);
        final double cosLat2 = Math.cos(lat2Rad);
        final double sinLat1 = Math.sin(lat1Rad);
        final double sinLat2 = Math.sin(lat2Rad);

        // Calculate the difference in longitudes.
        final double deltaLon = lon2Rad - lon1Rad;
        final double cosDeltaLon = Math.cos(deltaLon);
        final double sinDeltaLon = Math.sin(deltaLon);

        // Apply the Haversine formula components.
        final double y = Math.sqrt(Math.pow(cosLat2 * sinDeltaLon, 2)
                + Math.pow(cosLat1 * sinLat2 - sinLat1 * cosLat2 * cosDeltaLon, 2));
        final double x = sinLat1 * sinLat2 + cosLat1 * cosLat2 * cosDeltaLon;

        // Calculate the angular distance in radians.
        final double angularDistance = Math.atan2(y, x);

        // Convert angular distance to meters using the Earth's radius.
        return angularDistance * EARTH_RADIUS_METERS;
    }
}