package com.jumbotail.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jumbotail.shipping.enums.DeliverySpeed;
import com.jumbotail.shipping.enums.TransportMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for shipping charge calculation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Shipping charge calculation response")
public class ShippingChargeResponse {

    @Schema(description = "Total shipping charge in INR", example = "150.00")
    private BigDecimal shippingCharge;

    @Schema(description = "Transport mode selected based on distance", example = "TRUCK")
    private TransportMode transportMode;

    @Schema(description = "Delivery speed selected", example = "STANDARD")
    private DeliverySpeed deliverySpeed;

    @Schema(description = "Distance in kilometers", example = "245.5")
    private Double distanceKm;

    @Schema(description = "Weight used for calculation in kg", example = "5.0")
    private Double weightKg;

    @Schema(description = "Currency code", example = "INR")
    @Builder.Default
    private String currency = "INR";
}
