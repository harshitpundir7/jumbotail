package com.jumbotail.shipping.enums;

import lombok.Getter;

/**
 * Enum representing different transport modes based on distance.
 * Each mode has a specific rate per km per kg and minimum applicable distance.
 */
@Getter
public enum TransportMode {

    /**
     * Air transport for long distances (500+ km).
     * Rate: Rs 1 per km per kg
     */
    AEROPLANE("Aeroplane", 1.0, 500.0, "Air freight for long-distance shipping"),

    /**
     * Truck transport for medium distances (100-500 km).
     * Rate: Rs 2 per km per kg
     */
    TRUCK("Truck", 2.0, 100.0, "Road freight via truck for medium distances"),

    /**
     * Mini van transport for short distances (0-100 km).
     * Rate: Rs 3 per km per kg
     */
    MINI_VAN("Mini Van", 3.0, 0.0, "Local delivery via mini van for short distances");

    private final String displayName;
    private final double ratePerKmPerKg;
    private final double minDistanceKm;
    private final String description;

    TransportMode(String displayName, double ratePerKmPerKg, double minDistanceKm, String description) {
        this.displayName = displayName;
        this.ratePerKmPerKg = ratePerKmPerKg;
        this.minDistanceKm = minDistanceKm;
        this.description = description;
    }

    /**
     * Determines the appropriate transport mode based on distance.
     * 
     * @param distanceKm the distance in kilometers
     * @return the appropriate TransportMode
     */
    public static TransportMode getByDistance(double distanceKm) {
        if (distanceKm >= AEROPLANE.minDistanceKm) {
            return AEROPLANE;
        } else if (distanceKm >= TRUCK.minDistanceKm) {
            return TRUCK;
        } else {
            return MINI_VAN;
        }
    }

    /**
     * Calculates the transport charge for given distance and weight.
     * 
     * @param distanceKm distance in kilometers
     * @param weightKg   weight in kilograms
     * @return calculated charge in Rs
     */
    public double calculateCharge(double distanceKm, double weightKg) {
        return ratePerKmPerKg * distanceKm * weightKg;
    }
}
