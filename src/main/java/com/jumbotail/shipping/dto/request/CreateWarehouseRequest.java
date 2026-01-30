package com.jumbotail.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating a new Warehouse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new warehouse")
public class CreateWarehouseRequest {

    @NotBlank(message = "Warehouse code is required")
    @Size(max = 20, message = "Warehouse code cannot exceed 20 characters")
    @Schema(description = "Unique warehouse code", example = "HYD_WH_06")
    private String warehouseCode;

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 200, message = "Warehouse name cannot exceed 200 characters")
    @Schema(description = "Descriptive name", example = "Hyderabad Distribution Center")
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Schema(description = "Latitude coordinate", example = "17.3850")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Schema(description = "Longitude coordinate", example = "78.4867")
    private Double longitude;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Schema(description = "Full address", example = "Plot 45, Industrial Area, Hyderabad")
    private String address;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian pincode")
    @Schema(description = "6-digit pincode", example = "500032")
    private String pincode;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Schema(description = "City name", example = "Hyderabad")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Schema(description = "State name", example = "Telangana")
    private String state;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be positive")
    @Schema(description = "Storage capacity in square feet", example = "75000")
    private Integer capacitySqFt;

    @Size(max = 100, message = "Manager name cannot exceed 100 characters")
    @Schema(description = "Manager name (optional)", example = "Ravi Kumar")
    private String managerName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Schema(description = "Contact phone (optional)", example = "9876543210")
    private String contactPhone;
}
