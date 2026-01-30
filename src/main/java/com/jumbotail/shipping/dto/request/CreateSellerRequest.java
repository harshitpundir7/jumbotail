package com.jumbotail.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new Seller.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new seller")
public class CreateSellerRequest {

    @NotBlank(message = "Seller ID is required")
    @Size(max = 50, message = "Seller ID cannot exceed 50 characters")
    @Schema(description = "Unique seller identifier", example = "SELLER-006")
    private String sellerId;

    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    @Schema(description = "Business name", example = "Patanjali Ayurved Ltd")
    private String companyName;

    @Size(max = 100, message = "Contact name cannot exceed 100 characters")
    @Schema(description = "Contact person name (optional)", example = "Acharya Balkrishna")
    private String contactName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Schema(description = "10-digit phone number", example = "9876543215")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Schema(description = "Email address (optional)", example = "contact@patanjali.com")
    private String email;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Schema(description = "Latitude coordinate", example = "29.9457")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Schema(description = "Longitude coordinate", example = "78.1642")
    private Double longitude;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Schema(description = "Full address", example = "Patanjali Yogpeeth, Haridwar, Uttarakhand")
    private String address;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian pincode")
    @Schema(description = "6-digit pincode", example = "249405")
    private String pincode;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Schema(description = "City name", example = "Haridwar")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Schema(description = "State name", example = "Uttarakhand")
    private String state;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    @Schema(description = "GST number (optional)", example = "05AABCP1234F1Z5")
    private String gstNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    @Schema(description = "PAN number (optional)", example = "AABCP1234F")
    private String panNumber;
}
