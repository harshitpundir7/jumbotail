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
 * Response DTO for the combined shipping calculation API.
 * Includes both shipping charge and nearest warehouse information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Complete shipping calculation response with warehouse details")
public class ShippingCalculateResponse {

    @Schema(description = "Total shipping charge in INR", example = "180.00")
    private BigDecimal shippingCharge;

    @Schema(description = "Nearest warehouse details")
    private NearestWarehouseResponse nearestWarehouse;

    @Schema(description = "Transport mode selected based on distance", example = "AEROPLANE")
    private TransportMode transportMode;

    @Schema(description = "Delivery speed used for calculation", example = "EXPRESS")
    private DeliverySpeed deliverySpeed;

    @Schema(description = "Distance from warehouse to customer in km", example = "520.5")
    private Double distanceKm;

    @Schema(description = "Weight used for calculation in kg", example = "10.0")
    private Double weightKg;

    @Schema(description = "Currency code", example = "INR")
    @Builder.Default
    private String currency = "INR";
}
