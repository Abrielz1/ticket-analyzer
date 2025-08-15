package ru.abriel.ticket_analyzer.domain.model.geography;

/**
 * A Value Object representing a geographic point on Earth.
 * Combines Longitude and Latitude into a single, type-safe unit.
 * @param longitude The longitude of the point.
 * @param latitude The latitude of the point.
 */
public record GeoPoint(Longitude longitude, Latitude latitude) {}
