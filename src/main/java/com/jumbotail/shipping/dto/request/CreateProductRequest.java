package com.jumbotail.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating a new Product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new product")
public class CreateProductRequest {

    @NotBlank(message = "Product ID is required")
    @Size(max = 50, message = "Product ID cannot exceed 50 characters")
    @Schema(description = "Unique product SKU", example = "PROD-SUGAR-5KG")
    private String productId;

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    @Schema(description = "Product name", example = "Sugar 5kg Bag")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Product description (optional)", example = "Premium quality refined sugar")
    private String description;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category cannot exceed 100 characters")
    @Schema(description = "Product category", example = "Grocery")
    private String category;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Selling price must be positive")
    @Schema(description = "Selling price in INR", example = "250.00")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.01", message = "MRP must be positive")
    @Schema(description = "MRP in INR (optional)", example = "280.00")
    private BigDecimal mrp;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be positive")
    @Schema(description = "Weight in kg", example = "5.0")
    private Double weightInKg;

    @DecimalMin(value = "0.1", message = "Length must be positive")
    @Schema(description = "Length in cm (optional)", example = "30.0")
    private Double lengthCm;

    @DecimalMin(value = "0.1", message = "Width must be positive")
    @Schema(description = "Width in cm (optional)", example = "20.0")
    private Double widthCm;

    @DecimalMin(value = "0.1", message = "Height must be positive")
    @Schema(description = "Height in cm (optional)", example = "10.0")
    private Double heightCm;

    @NotNull(message = "Seller ID is required")
    @Schema(description = "ID of the seller", example = "1")
    private Long sellerId;

    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Stock quantity (optional, defaults to 0)", example = "100")
    private Integer stockQuantity;
}
