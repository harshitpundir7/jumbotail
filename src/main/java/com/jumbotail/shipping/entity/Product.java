package com.jumbotail.shipping.entity;

import com.jumbotail.shipping.entity.embeddable.ProductDimensions;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a Product listed on the marketplace.
 * Contains attributes crucial for shipping charge calculation (weight,
 * dimensions).
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_product_id", columnList = "productId", unique = true),
        @Index(name = "idx_product_seller", columnList = "seller_id"),
        @Index(name = "idx_product_category", columnList = "category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * SKU/Product code (e.g., "PROD-MAGGIE-500G").
     */
    @NotBlank(message = "Product ID is required")
    @Size(max = 50, message = "Product ID cannot exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String productId;

    /**
     * Product name.
     */
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Detailed product description.
     */
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    /**
     * Product category for classification.
     */
    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String category;

    /**
     * Selling price in INR.
     */
    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Selling price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    /**
     * Maximum Retail Price (MRP) in INR.
     */
    @DecimalMin(value = "0.01", message = "MRP must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid MRP format")
    @Column(precision = 12, scale = 2)
    private BigDecimal mrp;

    /**
     * Weight in kilograms - crucial for shipping calculation.
     */
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be positive")
    @Column(nullable = false)
    private Double weightInKg;

    /**
     * Product dimensions for volumetric weight calculation.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lengthCm", column = @Column(name = "dimension_length_cm")),
            @AttributeOverride(name = "widthCm", column = @Column(name = "dimension_width_cm")),
            @AttributeOverride(name = "heightCm", column = @Column(name = "dimension_height_cm"))
    })
    @Valid
    private ProductDimensions dimensions;

    /**
     * The seller offering this product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @ToString.Exclude
    private Seller seller;

    /**
     * Whether the product is available for ordering.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Stock quantity available.
     */
    @Builder.Default
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    /**
     * Timestamp of record creation.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of last update.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculates the chargeable weight for shipping.
     * Returns the greater of actual weight and volumetric weight.
     */
    public Double getChargeableWeight() {
        if (dimensions == null) {
            return weightInKg;
        }
        double volumetricWeight = dimensions.calculateVolumetricWeightKg();
        return Math.max(weightInKg, volumetricWeight);
    }
}
