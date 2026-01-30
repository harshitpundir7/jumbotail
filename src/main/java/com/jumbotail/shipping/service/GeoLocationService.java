package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for geographical calculations.
 * Uses the Haversine formula to calculate distances between two points on
 * Earth.
 */
@Service
@Slf4j
public class GeoLocationService {

    /**
     * Earth's radius in kilometers.
     */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the great-circle distance between two geographical points
     * using the Haversine formula.
     * 
     * The Haversine formula determines the great-circle distance between two points
     * on a sphere given their longitudes and latitudes.
     * 
     * @param from starting location
     * @param to   destination location
     * @return distance in kilometers
     * @throws IllegalArgumentException if either location is null
     */
    public double calculateDistanceInKm(GeoLocation from, GeoLocation to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both locations must be provided for distance calculation");
        }

        if (from.getLatitude() == null || from.getLongitude() == null ||
                to.getLatitude() == null || to.getLongitude() == null) {
            throw new IllegalArgumentException("Location coordinates cannot be null");
        }

        // Convert degrees to radians
        double lat1Rad = Math.toRadians(from.getLatitude());
        double lat2Rad = Math.toRadians(to.getLatitude());
        double deltaLatRad = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLonRad = Math.toRadians(to.getLongitude() - from.getLongitude());

        // Haversine formula
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        log.debug("Calculated distance from ({}, {}) to ({}, {}): {} km",
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude(),
                String.format("%.2f", distance));

        return distance;
    }

    /**
     * Checks if two locations are within a specified distance of each other.
     * 
     * @param from          first location
     * @param to            second location
     * @param maxDistanceKm maximum distance threshold
     * @return true if distance is within threshold
     */
    public boolean isWithinDistance(GeoLocation from, GeoLocation to, double maxDistanceKm) {
        double distance = calculateDistanceInKm(from, to);
        return distance <= maxDistanceKm;
    }

    /**
     * Calculates the bearing (direction) from one location to another.
     * 
     * @param from starting location
     * @param to   destination location
     * @return bearing in degrees (0-360, where 0 is North)
     */
    public double calculateBearing(GeoLocation from, GeoLocation to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both locations must be provided for bearing calculation");
        }

        double lat1Rad = Math.toRadians(from.getLatitude());
        double lat2Rad = Math.toRadians(to.getLatitude());
        double deltaLonRad = Math.toRadians(to.getLongitude() - from.getLongitude());

        double x = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double y = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);

        double bearing = Math.toDegrees(Math.atan2(x, y));

        // Normalize to 0-360 range
        return (bearing + 360) % 360;
    }
}
