package com.jumbotail.shipping.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Embeddable class representing product dimensions (length, width, height).
 * All dimensions are in centimeters.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDimensions implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.1", message = "Length must be positive")
    @Column(nullable = false)
    private Double lengthCm;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.1", message = "Width must be positive")
    @Column(nullable = false)
    private Double widthCm;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.1", message = "Height must be positive")
    @Column(nullable = false)
    private Double heightCm;

    /**
     * Calculates the volumetric weight in kg using standard formula.
     * Volumetric weight = (L × W × H) / 5000 for courier shipments.
     */
    public Double calculateVolumetricWeightKg() {
        return (lengthCm * widthCm * heightCm) / 5000.0;
    }

    /**
     * Returns a formatted dimension string (e.g., "10cm x 20cm x 15cm").
     */
    public String toDimensionString() {
        return String.format("%.1fcm x %.1fcm x %.1fcm", lengthCm, widthCm, heightCm);
    }
}
