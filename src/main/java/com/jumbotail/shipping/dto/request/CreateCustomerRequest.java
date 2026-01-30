package com.jumbotail.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new Customer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new customer")
public class CreateCustomerRequest {

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID cannot exceed 50 characters")
    @Schema(description = "Unique customer identifier", example = "CUST-006")
    private String customerId;

    @NotBlank(message = "Store name is required")
    @Size(max = 200, message = "Store name cannot exceed 200 characters")
    @Schema(description = "Name of the Kirana store", example = "Gupta General Store")
    private String storeName;

    @Size(max = 100, message = "Owner name cannot exceed 100 characters")
    @Schema(description = "Store owner name (optional)", example = "Ramesh Gupta")
    private String ownerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Schema(description = "10-digit phone number", example = "9876543220")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Schema(description = "Email address (optional)", example = "gupta.store@email.com")
    private String email;

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
    @Schema(description = "Full address", example = "Shop 12, Main Market, Secunderabad")
    private String address;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian pincode")
    @Schema(description = "6-digit pincode", example = "500003")
    private String pincode;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Schema(description = "City name", example = "Hyderabad")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Schema(description = "State name", example = "Telangana")
    private String state;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    @Schema(description = "GST number (optional)", example = "36AABCG1234F1Z5")
    private String gstNumber;
}
