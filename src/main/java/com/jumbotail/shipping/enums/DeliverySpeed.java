package com.jumbotail.shipping.enums;

import lombok.Getter;

/**
 * Enum representing delivery speed options.
 * Each speed has a base courier charge and an additional per-kg charge.
 */
@Getter
public enum DeliverySpeed {

    /**
     * Standard delivery with normal processing time.
     * Rs 10 standard courier charge + calculated shipping charge.
     */
    STANDARD("Standard", 10.0, 0.0, "Regular delivery (3-5 business days)"),

    /**
     * Express delivery with priority processing.
     * Rs 10 standard courier charge + Rs 1.2 per kg extra + calculated shipping
     * charge.
     */
    EXPRESS("Express", 10.0, 1.2, "Priority delivery (1-2 business days)");

    private final String displayName;
    private final double baseCourierCharge;
    private final double extraChargePerKg;
    private final String description;

    DeliverySpeed(String displayName, double baseCourierCharge, double extraChargePerKg, String description) {
        this.displayName = displayName;
        this.baseCourierCharge = baseCourierCharge;
        this.extraChargePerKg = extraChargePerKg;
        this.description = description;
    }

    /**
     * Calculates the delivery speed surcharge based on weight.
     * 
     * @param weightKg weight of the package in kilograms
     * @return total delivery speed charge (base + weight-based extra)
     */
    public double calculateDeliveryCharge(double weightKg) {
        return baseCourierCharge + (extraChargePerKg * weightKg);
    }

    /**
     * Parses a string value to DeliverySpeed enum (case-insensitive).
     * 
     * @param value the string value to parse
     * @return the corresponding DeliverySpeed
     * @throws IllegalArgumentException if the value is invalid
     */
    public static DeliverySpeed fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Delivery speed cannot be empty. Valid values: STANDARD, EXPRESS");
        }
        try {
            return DeliverySpeed.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid delivery speed: '%s'. Valid values: STANDARD, EXPRESS", value));
        }
    }
}
