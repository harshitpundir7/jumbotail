package com.jumbotail.shipping.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Embeddable class representing a geographical location with latitude and
 * longitude.
 * Used across Customer, Seller, and Warehouse entities.
 * 
 * Latitude ranges from -90 (South Pole) to +90 (North Pole).
 * Longitude ranges from -180 to +180 (Prime Meridian).
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(nullable = false)
    private Double longitude;

    /**
     * Returns a formatted string representation of the coordinates.
     */
    public String toCoordinateString() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}
