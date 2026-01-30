package com.jumbotail.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for combined shipping charge calculation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for calculating shipping charge from seller to customer")
public class ShippingCalculateRequest {

    @NotNull(message = "Seller ID is required")
    @Schema(description = "Seller's database ID", example = "1", required = true)
    private Long sellerId;

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer's database ID", example = "1", required = true)
    private Long customerId;

    @Schema(description = "Product ID for weight-based calculation (optional)", example = "1")
    private Long productId;

    @NotBlank(message = "Delivery speed is required")
    @Pattern(regexp = "^(STANDARD|EXPRESS|standard|express)$", message = "Delivery speed must be either 'STANDARD' or 'EXPRESS'")
    @Schema(description = "Delivery speed option", example = "EXPRESS", allowableValues = { "STANDARD",
            "EXPRESS" }, required = true)
    private String deliverySpeed;
}
