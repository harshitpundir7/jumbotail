package com.jumbotail.shipping.entity;

import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Warehouse in the marketplace network.
 * Warehouses are strategically located across India to optimize shipping.
 */
@Entity
@Table(name = "warehouses", indexes = {
        @Index(name = "idx_warehouse_code", columnList = "warehouseCode", unique = true),
        @Index(name = "idx_warehouse_city", columnList = "city"),
        @Index(name = "idx_warehouse_active", columnList = "isActive")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Unique warehouse code (e.g., "BLR_WH_01").
     */
    @NotBlank(message = "Warehouse code is required")
    @Size(max = 20, message = "Warehouse code cannot exceed 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String warehouseCode;

    /**
     * Descriptive name of the warehouse.
     */
    @NotBlank(message = "Warehouse name is required")
    @Size(max = 200, message = "Warehouse name cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Geographic location for distance calculations.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude", nullable = false)),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude", nullable = false))
    })
    @Valid
    private GeoLocation location;

    /**
     * Full address of the warehouse.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String address;

    /**
     * Postal pincode.
     */
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian pincode")
    @Column(nullable = false, length = 6)
    private String pincode;

    /**
     * City where warehouse is located.
     */
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;

    /**
     * State where warehouse is located.
     */
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String state;

    /**
     * Storage capacity in square feet.
     */
    @Min(value = 1, message = "Capacity must be positive")
    @Column(nullable = false)
    private Integer capacitySqFt;

    /**
     * Current utilization percentage (0-100).
     */
    @Builder.Default
    @Min(value = 0, message = "Utilization cannot be negative")
    @Column(nullable = false)
    private Integer utilizationPercent = 0;

    /**
     * Manager/contact person name.
     */
    @Size(max = 100, message = "Manager name cannot exceed 100 characters")
    @Column(length = 100)
    private String managerName;

    /**
     * Contact phone number.
     */
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Column(length = 15)
    private String contactPhone;

    /**
     * Whether the warehouse is operational.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

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
}
